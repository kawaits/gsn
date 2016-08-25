# -*- coding: UTF-8 -*-
from _ctypes import Array
__author__      = "Tonio Gsell <tgsell@tik.ee.ethz.ch>"
__copyright__   = "Copyright 2010, ETH Zurich, Switzerland, Tonio Gsell"
__license__     = "GPL"
__version__     = "$Revision$"
__date__        = "$Date$"
__id__          = "$Id$"

import logging
import time
import Queue
import struct
import array
from threading import Thread, Lock, Event

import DPPTypes
from smbus import SMBus
from BackLogMessage import PLUGIN_MESSAGE_TYPES
from SpecialAPI import Statistics

# Interval a time synchronization message is sent to DPP in seconds.
TIMESYNC_INTERVAL_SEC = 10.0

DEFAULT_BACKLOG = True

'''
 The I2C commands
'''
I2C_CMD_INVALID = 0
I2C_CMD_READ_MSG = 1
I2C_CMD_TIMESYNC = 2

I2C_MSG_HEADER_START = 1
I2C_MSG_HEADER_LENGTH = 14
I2C_MSG_PAYLOAD_START = 15

SEND_QUEUE_SIZE = 25

class DPPPeerClass(Thread, Statistics):
    '''
    Offers the functionality to communicate with the MSP432 over i2c on DPP.payload|
    '''

    '''
    data/instance attributes:
    _backlogMain
    _i2cBus
    _i2cAddress
    _dppWriter
    _logger 
    _dppPeerStop
    _workEvent
    _i2cPollingIntervalSec
    '''
    
    def __init__(self, parent, i2cDevice, i2cAddress, i2cPollingIntervalSec):
        Thread.__init__(self, name='DPPPeer-Thread')
        self._logger = logging.getLogger(self.__class__.__name__)
        Statistics.__init__(self)
        
        self._i2cLock = Lock()
        self._i2cAddress = i2cAddress
        self._i2cPollingIntervalSec = i2cPollingIntervalSec
        try:
            self._i2cBus = SMBus(i2cDevice)
        except Exception, e:
            raise TypeError('could not initialize I2C bus: %s' % (e,))
        
        self._msgSentCounterId = self.createCounter()
        self._msgReceivedCounterId = self.createCounter()
        self._ackSentCounterId = self.createCounter()
        
        self._dppWriter = DPPWriter(self)
        
        self._timesynctimer = TimeSyncTimer(TIMESYNC_INTERVAL_SEC, self.timesync)

        self._backlogMain = parent
        self._dppPeerStop = False
        
        self._workEvent = Event()
            
        
    def run(self):
        try:
            self._logger.info('started')
    
            self._dppWriter.start()
            self._timesynctimer.start()
            
            # speed optimizations
            acquire = self._i2cLock.acquire
            read = self._i2cBus.read_i2c_block_data
            release = self._i2cLock.release
            processDPPMsg = self._backlogMain.processDPPMsg
            isEnabledFor = self._logger.isEnabledFor
            
            while not self._dppPeerStop:
                # read packet from I2C bus
                try:
                    if isEnabledFor(logging.DEBUG):
                        self._logger.debug('rcv...')
                    acquire()
                    msg = array.array('B', read(self._i2cAddress, I2C_CMD_READ_MSG)).tostring()
                    timestamp = int(time.time()*1000)
                    release()
                except Exception, e:
                    release()
                    if not self._dppPeerStop:
                        self.exception('could not read from I2C bus: %s' % (e,))
                    self._workEvent.wait(self._i2cPollingIntervalSec)
                    continue
                
                (msgBufSize, ) = struct.unpack('<B',msg[:1])
                
                # is there a message?
                if msgBufSize == 0:
                    self._workEvent.wait(self._i2cPollingIntervalSec)
                    continue
                
                msgHeader = struct.unpack('<HBBHQ',msg[I2C_MSG_HEADER_START:I2C_MSG_HEADER_START+I2C_MSG_HEADER_LENGTH])
                dppMsg = dict(device_id=msgHeader[0], type=msgHeader[1], payload_len=msgHeader[2], seqnr=msgHeader[3], generation_time=msgHeader[4], payload=bytearray(msg[I2C_MSG_PAYLOAD_START:I2C_MSG_PAYLOAD_START+msgHeader[2]]))
                
                if isEnabledFor(logging.DEBUG):
                    self._logger.debug('rcv (%d,%d,%d,%d,%d,%d,%d)' % (timestamp, dppMsg['device_id'], dppMsg['type'], dppMsg['payload_len'], dppMsg['seqnr'], dppMsg['generation_time'], len(dppMsg['payload'])))
                    self._logger.debug('payload [' + ','.join(str(int(x)) for x in dppMsg['payload']) + ']')
    
                self.counterAction(self._msgReceivedCounterId)
                # tell BackLogMain to send the packet to the plugins
                # TODO: implement proper message acknowledge and make sure no messages can get lost
                if processDPPMsg(timestamp, dppMsg):
                    self.counterAction(self._ackSentCounterId)
                
                if --msgBufSize == 0:
                    # wait for the next poll
                    self._workEvent.wait(self._i2cPollingIntervalSec)
                            
            self._timesynctimer.join()
            self._dppWriter.join()
        except Exception, e:
            self.exception(str(e))
    
        self._logger.info('died')
        
            
    def sendDPPMsg(self, dppMsg):
        if not self.isAlive():
            raise Exception("DPPPeer is not alive")
        return self._dppWriter.addMsg(dppMsg)


    def timesync(self):
        try:
            #TODO: set own device_id?
            packet = dict(device_id=0, type=DPPTypes.MSG_TYPE_TIMESYNC, payload_len=0, seqnr=0, generation_time=None, payload=None)
            self._dppWriter.addMsg(packet)
        except Exception, e:
            self.exception(str(e))
            
            
    def getStatus(self):
        '''
        Returns the status of the DPP peer as list:
        
        @return: status of the DPP peer [received message counter,
                                         sent acknowledge counter,
                                         sent message counter]
        '''
        return [self.getCounterValue(self._msgReceivedCounterId), \
                self.getCounterValue(self._ackSentCounterId), \
                self.getCounterValue(self._msgSentCounterId)]


    def exception(self, exception):
        self._backlogMain.incrementExceptionCounter()
        self._logger.exception(str(exception))

        
    def stop(self):
        self._dppPeerStop = True
        self._timesynctimer.stop()
        self._workEvent.set()
        self._dppWriter.stop()
        self._logger.info('stopped')



class DPPWriter(Thread):

    '''
    data/instance attributes:
    _logger
    _dppPeer
    _sendqueue
    _dppWriterStop
    '''

    def __init__(self, parent):
        Thread.__init__(self, name='%s-Thread' % (self.__class__.__name__,))
        self._logger = logging.getLogger(self.__class__.__name__)
        self._dppPeer = parent
        self._sendqueue = Queue.Queue(SEND_QUEUE_SIZE)
        self._dppWriterStop = False


    def run(self):
        self._logger.info('started')
        
        # speed optimizations
        acquire = self._dppPeer._i2cLock.acquire
        write = self._dppPeer._i2cBus.write_i2c_block_data
        release = self._dppPeer._i2cLock.release
        get = self._sendqueue.get
        isEnabledFor = self._logger.isEnabledFor
        
        while not self._dppWriterStop:
            dppMsg = get()
            if self._dppWriterStop:
                break
            
            try:
                if dppMsg['type'] == DPPTypes.MSG_TYPE_TIMESYNC:
                    if isEnabledFor(logging.DEBUG):
                        self._logger.debug('write MSG_TYPE_TIMESYNC message (%d,%d,%d,%d,%d)' % (dppMsg['device_id'], dppMsg['type'], dppMsg['payload_len'], dppMsg['seqnr'], int(time.time()*1000000)))
                    packet = array.array('B', struct.pack('<BHBBHQ', self._sendqueue.qsize(), dppMsg['device_id'], dppMsg['type'], dppMsg['payload_len'], dppMsg['seqnr'], int(time.time()*1000000)))
                    acquire()
                    write(self._dppPeer._i2cAddress, I2C_CMD_TIMESYNC, packet.tolist())
                else:
                    if isEnabledFor(logging.DEBUG):
                        self._logger.debug('write message with type %d' % dppMsg['type'])
                    packet = array.array('B', struct.pack('<BHBBHQ%dc' % dppMsg['payload_len'], self._sendqueue.qsize(), dppMsg['device_id'], dppMsg['type'], dppMsg['payload_len'], dppMsg['seqnr'], dppMsg['generation_time'], dppMsg['payload'])).tolist()
                    acquire()
                    write(self._dppPeer._i2cAddress, I2C_CMD_READ_MSG, packet)
                    
                self._dppPeer.counterAction(self._dppPeer._msgSentCounterId)
                if isEnabledFor(logging.DEBUG):
                    self._logger.debug('snd (%d,?,%d)' % (PLUGIN_MESSAGE_TYPES['DPPPlugin'], len(packet)))
            except Exception, e:
                if not self._dppWriterStop:
                    self._logger.warning('could not write message to I2C bus: %s' % (e,))
            finally:
                release()
                self._sendqueue.task_done()
 
        self._logger.info('died')


    def stop(self):
        self._dppWriterStop = True
        try:
            self._sendqueue.put_nowait('end')
        except Queue.Full:
            pass
        except Exception, e:
            self._logger.exception(e)
        self._logger.info('stopped')


    def addMsg(self, packet):
        if not self._dppWriterStop:
            try:
                self._sendqueue.put_nowait(packet)
            except Queue.Full:
                self._logger.warning('DPP send queue is full')
                return False
        return True



class TimeSyncTimer(Thread):
    
    '''
    data/instance attributes:
    _logger
    _action
    _wait
    _timer
    _timeSyncTimerStop
    '''
    
    def __init__(self, interval, action):
        Thread.__init__(self, name='%s-Thread' % (self.__class__.__name__,))
        self._logger = logging.getLogger(self.__class__.__name__)
        self._action = action
        self._wait = interval
        self._timer = Event()
        self._timeSyncTimerStop = False
        
           
    def run(self):
        self._logger.info('started')
        while not self._timeSyncTimerStop:
            self._logger.debug('action')
            self._action()
            self._timer.wait(self._wait)
        self._logger.info('died')
    
    
    def stop(self):
        self._timeSyncTimerStop = True
        self._timer.set()
        self._logger.info('stopped')
        