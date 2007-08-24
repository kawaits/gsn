package gsn.beans.windowing;

import gsn.beans.StreamElement;
import gsn.beans.StreamSource;
import gsn.storage.SQLUtils;
import gsn.storage.StorageManager;
import gsn.utils.CaseInsensitiveComparator;
import gsn.utils.GSNRuntimeException;
import gsn.wrappers.AbstractWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class TupleBasedSlidingHandler implements SlidingHandler {
	private static final transient Logger logger = Logger.getLogger(TupleBasedSlidingHandler.class);

	private ArrayList<StreamSource> streamSources;

	private HashMap<StreamSource, Integer> slidingHashMap;

	private AbstractWrapper wrapper;

	public TupleBasedSlidingHandler(AbstractWrapper wrapper) {
		streamSources = new ArrayList<StreamSource>();
		slidingHashMap = new HashMap<StreamSource, Integer>();
		this.wrapper = wrapper;
	}

	public void addStreamSource(StreamSource streamSource) {
		streamSources.add(streamSource);
		if (streamSource.getWindowingType() != WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE) {
			if (streamSource.getWindowingType() == WindowType.TUPLE_BASED)
				slidingHashMap.put(streamSource, streamSource.getParsedSlideValue() - streamSource.getParsedStorageSize());
			else
				slidingHashMap.put(streamSource, 0);
		}
		SQLViewQueryRewriter rewriter = new TupleBasedSQLViewQueryRewriter();
		rewriter.setStreamSource(streamSource);
		rewriter.initialize();
	}

	public boolean dataAvailable(StreamElement streamElement) {
		boolean toReturn = false;
		synchronized (streamSources) {
			for (StreamSource streamSource : streamSources) {
				if (streamSource.getWindowingType() == WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE)
					toReturn = streamSource.getQueryRewriter().dataAvailable(streamElement.getTimeStamp()) || toReturn;
				else {
					int slideVar = slidingHashMap.get(streamSource) + 1;
					if (slideVar == streamSource.getParsedSlideValue()) {
						toReturn = streamSource.getQueryRewriter().dataAvailable(streamElement.getTimeStamp()) || toReturn;
						slideVar = 0;
					}
					slidingHashMap.put(streamSource, slideVar);
				}
			}
		}
		return toReturn;
	}

	public long getOldestTimestamp() {
		long timed1 = -1;
		long timed2 = -1;
		int maxTupleCount = 0;
		int maxTupleForTimeBased = 0;
		int maxWindowSize = 0;
		synchronized (streamSources) {
			for (StreamSource streamSource : streamSources) {
				if (streamSource.getWindowingType() != WindowType.TIME_BASED_WIN_TUPLE_BASED_SLIDE)
					maxTupleCount = Math.max(maxTupleCount, streamSource.getParsedStorageSize());
				else {
					maxTupleForTimeBased = Math.max(maxTupleForTimeBased, streamSource.getParsedSlideValue());
					maxWindowSize = Math.max(maxWindowSize, streamSource.getParsedStorageSize());
				}
				if (streamSource.getWindowingType() == WindowType.TUPLE_BASED)
					maxTupleCount = Math.max(maxTupleCount, streamSource.getParsedStorageSize() + streamSource.getParsedSlideValue());
			}
		}

		if (maxTupleCount > 0) {
			StringBuilder query = new StringBuilder();
			if (StorageManager.isHsql() || StorageManager.isMysqlDB()) {
				query.append(" select timed from ").append(wrapper.getDBAliasInStr());
				query.append(" order by timed desc limit 1 offset ").append(maxTupleCount - 1);
			} else if (StorageManager.isSqlServer()) {
				query.append(" select min(timed) from (select top ").append(maxTupleCount).append(" * ").append(" from ").append(
						wrapper.getDBAliasInStr()).append(" order by timed desc )as X  ");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Query1 for getting oldest timestamp : " + query);
			}
			ResultSet resultSet = null;
			try {
				 resultSet = StorageManager.getInstance().executeQueryWithResultSet(query);
				if (resultSet.next())
					timed1 = resultSet.getLong(1);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}finally {
			  StorageManager.close(resultSet);
			}
		}

		if (maxWindowSize > 0) {
			StringBuilder query = new StringBuilder();
			if (StorageManager.isHsql() || StorageManager.isMysqlDB()) {
				query.append(" select timed - ").append(maxWindowSize).append(" from (select timed from ").append(
						wrapper.getDBAliasInStr());
				query.append(" order by timed desc limit 1 offset ").append(maxTupleForTimeBased - 1).append(" ) as X ");
			} else if (StorageManager.isSqlServer()) {
				query.append(" select min(timed) - ").append(maxWindowSize).append(" from (select top ").append(maxTupleForTimeBased)
						.append(" * ").append(" from ").append(wrapper.getDBAliasInStr()).append(" order by timed desc ) as X  ");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Query2 for getting oldest timestamp : " + query);
			}
			ResultSet resultSet = null ;
			try {
				resultSet = StorageManager.getInstance().executeQueryWithResultSet(query);
				if (resultSet.next())
					timed2 = resultSet.getLong(1);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}finally {
        StorageManager.close(resultSet);
      }
		}

		if (timed1 >= 0 && timed2 >= 0)
			return Math.min(timed1, timed2);
		return (timed1 == -1) ? timed2 : timed1;
	}

	public void removeStreamSource(StreamSource streamSource) {
		streamSources.remove(streamSource);
		slidingHashMap.remove(streamSource);
		streamSource.getQueryRewriter().finilize();
	}

	public void finilize() {
		synchronized (streamSources) {
			for (Iterator<StreamSource> iterator = streamSources.iterator(); iterator.hasNext();) {
				StreamSource streamSource = iterator.next();
				streamSource.getQueryRewriter().finilize();
				iterator.remove();
				slidingHashMap.remove(streamSource);
			}
		}
	}

	public boolean isInterestingIn(StreamSource streamSource) {
		return WindowType.isTupleBased(streamSource.getWindowingType());
	}

	private class TupleBasedSQLViewQueryRewriter extends SQLViewQueryRewriter {

		@Override
		public CharSequence createViewSQL() {
			if (cachedSqlQuery != null)
				return cachedSqlQuery;
			if (streamSource.getWrapper() == null)
				throw new GSNRuntimeException("Wrapper object is null, most probably a bug, please report it !");
			if (streamSource.validate() == false)
				throw new GSNRuntimeException("Validation of this object the stream source failed, please check the logs.");
			CharSequence wrapperAlias = streamSource.getWrapper().getDBAliasInStr();
			int windowSize = streamSource.getParsedStorageSize();
			if (streamSource.getSamplingRate() == 0 || windowSize == 0)
				return cachedSqlQuery = new StringBuilder("select * from ").append(wrapperAlias).append(" where 1=0");
			TreeMap<CharSequence, CharSequence> rewritingMapping = new TreeMap<CharSequence, CharSequence>(new CaseInsensitiveComparator());
			rewritingMapping.put("wrapper", wrapperAlias);
			StringBuilder toReturn = new StringBuilder(streamSource.getSqlQuery());
			if (streamSource.getSqlQuery().toLowerCase().indexOf(" where ") < 0)
				toReturn.append(" where ");
			else
				toReturn.append(" and ");
			// Applying the ** START AND END TIME ** for all types of windows
			// based
			// windows
			toReturn.append(" wrapper.timed >=").append(streamSource.getStartDate().getTime()).append(" and timed <=").append(
					streamSource.getEndDate().getTime());

			if (streamSource.getSamplingRate() != 1)
				toReturn.append(" and ( mod( timed , 100)< ").append(streamSource.getSamplingRate() * 100).append(")");

			toReturn.append(" and ");

			WindowType windowingType = streamSource.getWindowingType();
			if (windowingType == WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE) {
				if (StorageManager.isHsql() || StorageManager.isMysqlDB())
					toReturn.append("timed >= (select timed from ").append(wrapperAlias).append(
							" order by timed desc limit 1 offset ").append(windowSize - 1).append(" ) order by timed desc ");
				else if (StorageManager.isSqlServer())
					toReturn.append("timed >= (select min(timed) from (select TOP ").append(windowSize).append(
							" timed from ").append(wrapperAlias)
							.append(" order by timed desc ) as y )");
			} else {
				if (windowingType == WindowType.TUPLE_BASED) {
					if (StorageManager.isHsql() || StorageManager.isMysqlDB()) {
						toReturn.append("timed <= (select timed from ").append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(
								" where UID='").append(streamSource.getUIDStr()).append("') and timed >= (select timed from ");
						toReturn.append(wrapperAlias).append(" where timed <= (select timed from ");
						toReturn.append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(" where UID='").append(streamSource.getUIDStr());
						toReturn.append("') ").append(" order by timed desc limit 1 offset ").append(windowSize - 1).append(" )");
						toReturn.append(" order by timed desc ");
					} else if (StorageManager.isSqlServer()) {
						toReturn.append("timed in (select TOP ").append(windowSize).append(" timed from ").append(wrapperAlias).append(
								" where timed <= (select timed from ").append(SQLViewQueryRewriter.VIEW_HELPER_TABLE)
								.append(" where UID='").append(streamSource.getUIDStr()).append("') order by timed desc) ");
					}
				} else { // WindowType.TIME_BASED_WIN_TUPLE_BASED_SLIDE
					toReturn.append("timed in (select timed from ").append(wrapperAlias).append(" where timed <= (select timed from ")
							.append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(" where UID='").append(streamSource.getUIDStr()).append(
									"') and timed >= (select timed from ").append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(
									" where UID='").append(streamSource.getUIDStr()).append("') - ").append(windowSize).append(" ) ");
					if (StorageManager.isHsql() || StorageManager.isMysqlDB())
						toReturn.append(" order by timed desc ");
				}
			}

			toReturn = new StringBuilder(SQLUtils.newRewrite(toReturn, rewritingMapping));
			if (logger.isDebugEnabled()) {
				logger.debug(new StringBuilder().append("The original Query : ").append(streamSource.getSqlQuery()).toString());
				logger.debug(new StringBuilder().append("The merged query : ").append(toReturn.toString()).append(" of the StreamSource ")
						.append(streamSource.getAlias()).append(" of the InputStream: ").append(
								streamSource.getInputStream().getInputStreamName()).append("").toString());
			}
			return cachedSqlQuery = toReturn;
		}
	}

}
