package de.ladam.template.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

import de.ladam.template.util.application.ApplicationLogger;

public enum DBConnector {

	INSTANCE;

	private Map<String, DataSource> datasourcesMap;
	private InitialContext ctx = null;
	private Properties properties;

	/**
	 * default
	 */
	public static String DATASOURCE = null;
	public static String DATASOURCE_MYSQL = "jdbc/mysql_local";
	public static String DATASOURCE_PSQL = "jdbc/psql_local";

	static {
		DATASOURCE = DATASOURCE_PSQL;
	}

	private DBConnector() {
		datasourcesMap = new HashMap<>();
		properties = System.getProperties();

//		try {
//			PropertyFileReader fileReader = new PropertyFileReader();
//			properties = fileReader.readPropertyFile("sql.properties");
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			properties = System.getProperties();
//		}

		try {
			ctx = new InitialContext();
		} catch (NamingException e) {
			ctx = null;
		}

	}

	public static void reset() {
		INSTANCE._reset();
	}

	private void _reset() {
		datasourcesMap.forEach((k, ds) -> {
			ds = null;
		});

		datasourcesMap.clear();
		datasourcesMap = new HashMap<String, DataSource>();
	}

	public static boolean isAvailable(String datasource) {
		return INSTANCE._isAvailable(datasource);
	}

	public static boolean isAvailable() {
		return INSTANCE._isAvailable(DATASOURCE);
	}

	private boolean _isAvailable(String datasource) {
		boolean available = false;
		Connection con = null;

		try {
			con = _getConnection(datasource);
			available = con != null;

		} catch (Exception e) {
		} finally {
			_close(con);
		}

		return available;
	}

	public static Connection getConnection() {
		return getConnection(DATASOURCE);
	}

	public static Connection getConnection(String datasource) {

		return INSTANCE._getConnection(datasource);

	}

	private Connection _getConnection(String datasource) {
		DataSource ds = null;
		Connection con = null;

		synchronized (INSTANCE) {

			if (!datasourcesMap.containsKey(datasource)) {

				try {
					ds = (DataSource) ctx.lookup(datasource);
				} catch (NamingException e) {
					ApplicationLogger.warn(e.getMessage() + ": " + datasource);
				}

				if (ds == null) {
					ds = createDatasource(datasource);
				}

				if (ds != null) {
					datasourcesMap.put(datasource, ds);
				}
			} else {
				ds = datasourcesMap.get(datasource);
			}

		}

		try {
			if (ds != null) {
				con = ds.getConnection();
			}
		} catch (SQLException e) {
			ApplicationLogger.fatal("@DBC_GC" + e.getMessage());
		}
		if (con == null) {
			ApplicationLogger.error("Connection is null");
		} else {
			// Debug Control
		}

		return con;
	}

	private DataSource createDatasource(String datasource) {
		DataSource ds = null;
		if (datasource.equals("jdbc/psql_local")) {

			ApplicationLogger.debug("loading system datasource manually jdbc/devlocal");
			String driverClassname = "org.postgresql.ds.PGSimpleDataSource";

			try {
				String prefix = datasource.replaceFirst("java:", "");

				String url = properties.getProperty(prefix + ".Url");
				String user = properties.getProperty(prefix + ".User");
				String password = properties.getProperty(prefix + ".Password");

				if (url == null || user == null || password == null) {
					ApplicationLogger.error("JDBC has one or more null value");
					return null;
				}

				boolean driverAvailable = true;
				try {
					Class.forName(driverClassname);
				} catch (ClassNotFoundException e) {

					ApplicationLogger.warn("no driver available");
					driverAvailable = false;
				}

				if (driverAvailable) {

					PGSimpleDataSource PGDS = new PGSimpleDataSource();

					PGDS.setUrl(url);
					PGDS.setUser(user);
					PGDS.setPassword(password);
					ds = (DataSource) PGDS;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (datasource.equals("jdbc/mysql_local")) {

			ApplicationLogger.debug("loading system datasource manually jdbc/devlocal");
			String driverClassname = "com.mysql.cj.jdbc.MysqlDataSource";

			try {
				String prefix = datasource.replaceFirst("java:", "");

				String databaseName = properties.getProperty(prefix + ".DatabaseName");
				String serverName = properties.getProperty(prefix + ".ServerName");
				String user = properties.getProperty(prefix + ".User");
//				String password = properties.getProperty(prefix + ".Password");
				String portnumber = properties.getProperty(prefix + ".PortNumber");
//				String maxAllowedPacketString = properties.getProperty(prefix + ".maxAllowedPacket");
//				String serverTimezone = properties.getProperty(prefix + ".serverTimezone");

				int port = 0;
				try {
					port = Integer.parseInt(portnumber);
				} catch (NumberFormatException e) {
				}

//				int maxAllowedPacket = 0;
//				try {
//					maxAllowedPacket = Integer.parseInt(maxAllowedPacketString);
//				} catch (NumberFormatException e) {
//				}

				if (databaseName == null || serverName == null || user == null /* || password == null */ || port == 0
				/* || maxAllowedPacket == 0 */) {
					ApplicationLogger.error("JDBC has one or more null value");
					return null;
				}

				boolean driverAvailable = true;
				try {
					Class.forName(driverClassname);
				} catch (ClassNotFoundException e) {

					ApplicationLogger.warn("no driver available");
					driverAvailable = false;
				}

				if (driverAvailable) {

					MysqlDataSource mySQLDS = new MysqlDataSource();

					mySQLDS.setDatabaseName(databaseName);
					mySQLDS.setServerName(serverName);
					mySQLDS.setUser(user);
					mySQLDS.setPortNumber(port);
					ds = (DataSource) mySQLDS;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return ds;
	}

	public static void execute(String datasource, Consumer<Connection> action) {
		INSTANCE._execute(datasource, action);
	}

	public static void prepare(String datasource, String statement, Consumer<PreparedStatement> action) {
		try {
			INSTANCE._prepare(datasource, statement, action);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static <T> T prepareResult(String datasource, String statement, Function<PreparedStatement, T> action) {
		try {
			return INSTANCE._prepareResult(datasource, statement, false, action);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T prepareResult(String datasource, String statement, boolean returnKeys,
			Function<PreparedStatement, T> action) {
		try {
			return INSTANCE._prepareResult(datasource, statement, returnKeys, action);
		} catch (Exception e) {
			return null;
		}
	}

	private <T> T _prepareResult(String datasource, String statement, boolean returnKeys,
			Function<PreparedStatement, T> action) {

		Connection con = null;
		PreparedStatement pstm = null;
		T output = null;

		if (action == null || statement == null) {
			return output;
		}

		try {

			con = getConnection(datasource);

			try {

				if (con != null) {

					pstm = returnKeys ? con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)
							: con.prepareStatement(statement);

					if (pstm != null) {
						output = action.apply(pstm);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				_closePreparedStatement(pstm);
			}

		} catch (Exception e) {

		} finally {
			_close(con);
		}

		return output;

	}

	private void _prepare(String datasource, String statement, Consumer<PreparedStatement> action) throws Exception {

		Connection con = null;
		PreparedStatement pstm = null;

		if (action == null || statement == null)
			return;

		try {

			con = _getConnection(datasource);

			try {

				if (con != null) {

					pstm = con.prepareStatement(statement);

					if (action != null && pstm != null) {
						action.accept(pstm);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				_closePreparedStatement(pstm);
			}

		} catch (Exception e) {

		} finally {
			_close(con);
		}

	}

	private void _execute(String datasource, Consumer<Connection> action) {

		Connection con = null;

		try {
			con = getConnection(datasource);

			if (con != null || action != null) {
				action.accept(con);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			_close(con);
		}

	}

	public void closeStatement(Statement stm) {
		INSTANCE._closeStatement(stm);
	}

	private void _closeStatement(Statement stm) {

		if (stm != null) {

			try {
				stm.close();
				stm = null;
			} catch (SQLException e) {
				stm = null;
			}

		}

	}

	public static void closePreparedStatement(PreparedStatement stm) {
		INSTANCE._closePreparedStatement(stm);
	}

	private void _closePreparedStatement(PreparedStatement stm) {

		if (stm == null)
			return;

		try {

			if (!stm.isClosed()) {

				stm.clearBatch();
				stm.clearParameters();
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		} finally {

			try {
				stm.close();
				stm = null;
			} catch (Exception e) {
				e.printStackTrace();
				stm = null;
			}
		}

	}

	public static void close(Connection con) {
		INSTANCE._close(con);
	}

	private void _close(Connection con) {

		if (con != null) {

			// rollback uncommited changes prior to close
			try {
				if (!con.getAutoCommit()) {
					con.rollback();
				}
			} catch (Exception e) {
			} finally {

				try {
					con.close();
					con = null;
				} catch (Exception e) {
					con = null;
				}
				// DebugControl
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		INSTANCE._closeResultSet(rs);
	}

	private void _closeResultSet(ResultSet rs) {

		if (rs != null) {

			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				rs = null;
			}

		}

	}

	public static Connection setAutoCommit(Connection con, boolean autoCommit) {
		if (con != null) {
			try {
				con.setAutoCommit(autoCommit);
			} catch (Exception e) {
			}
		}
		return con;
	}

	@FunctionalInterface
	public interface SqlConsumer<T> {

		void accept(T t) throws SQLException;

	}

}
