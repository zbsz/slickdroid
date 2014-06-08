package scala.slick.android

import android.database.sqlite.SQLiteDatabase
import java.sql._
import java.{util, sql}
import java.util.concurrent.Executor
import scala.Array
import java.util.Properties

/**
  */
class SlickDroidConnection(val db: SQLiteDatabase) extends java.sql.Connection {

  private var autoCommit = true

  override def createStatement(): Statement = ???

  override def setAutoCommit(p1: Boolean): Unit = autoCommit = p1

  override def setHoldability(p1: Int): Unit = ???

  override def clearWarnings(): Unit = ???

  override def getNetworkTimeout: Int = ???

  override def createBlob(): Blob = ???

  override def createSQLXML(): SQLXML = ???

  override def setSavepoint(): Savepoint = ???

  override def setSavepoint(p1: String): Savepoint = ???

  override def createNClob(): NClob = ???

  override def getTransactionIsolation: Int = ???

  override def getClientInfo(p1: String): String = ???

  override def getClientInfo: Properties = ???

  override def getSchema: String = ???

  override def setNetworkTimeout(p1: Executor, p2: Int): Unit = ???

  override def getMetaData: DatabaseMetaData = new DatabaseMetaData {override def supportsMultipleOpenResults(): Boolean = ???

    override def supportsSubqueriesInIns(): Boolean = ???

    override def getSuperTypes(p1: String, p2: String, p3: String): ResultSet = ???

    override def getTablePrivileges(p1: String, p2: String, p3: String): ResultSet = ???

    override def supportsFullOuterJoins(): Boolean = ???

    override def insertsAreDetected(p1: Int): Boolean = ???

    override def getDriverMajorVersion: Int = ???

    override def getDatabaseProductVersion: String = ???

    override def getIndexInfo(p1: String, p2: String, p3: String, p4: Boolean, p5: Boolean): ResultSet = ???

    override def getFunctionColumns(p1: String, p2: String, p3: String, p4: String): ResultSet = ???

    override def supportsCatalogsInTableDefinitions(): Boolean = ???

    override def isCatalogAtStart: Boolean = ???

    override def getJDBCMinorVersion: Int = ???

    override def supportsMixedCaseQuotedIdentifiers(): Boolean = ???

    override def storesUpperCaseQuotedIdentifiers(): Boolean = ???

    override def getUDTs(p1: String, p2: String, p3: String, p4: Array[Int]): ResultSet = ???

    override def getAttributes(p1: String, p2: String, p3: String, p4: String): ResultSet = ???

    override def supportsStoredFunctionsUsingCallSyntax(): Boolean = ???

    override def nullsAreSortedAtStart(): Boolean = ???

    override def getMaxIndexLength: Int = ???

    override def getMaxTablesInSelect: Int = ???

    override def getClientInfoProperties: ResultSet = ???

    override def supportsSchemasInDataManipulation(): Boolean = ???

    override def getDatabaseMinorVersion: Int = ???

    override def supportsSchemasInProcedureCalls(): Boolean = ???

    override def supportsOuterJoins(): Boolean = ???

    override def supportsGroupBy(): Boolean = ???

    override def doesMaxRowSizeIncludeBlobs(): Boolean = ???

    override def supportsCatalogsInDataManipulation(): Boolean = ???

    override def getDatabaseProductName: String = ???

    override def supportsOpenCursorsAcrossCommit(): Boolean = ???

    override def supportsTableCorrelationNames(): Boolean = ???

    override def supportsExtendedSQLGrammar(): Boolean = ???

    override def getJDBCMajorVersion: Int = ???

    override def getUserName: String = ???

    override def getMaxProcedureNameLength: Int = ???

    override def getDriverName: String = ???

    override def getMaxRowSize: Int = ???

    override def dataDefinitionCausesTransactionCommit(): Boolean = ???

    override def getMaxColumnNameLength: Int = ???

    override def getMaxSchemaNameLength: Int = ???

    override def getVersionColumns(p1: String, p2: String, p3: String): ResultSet = ???

    override def getNumericFunctions: String = ???

    override def supportsIntegrityEnhancementFacility(): Boolean = ???

    override def getIdentifierQuoteString: String = ???

    override def supportsNonNullableColumns(): Boolean = ???

    override def getMaxConnections: Int = ???

    override def supportsResultSetHoldability(p1: Int): Boolean = ???

    override def supportsGroupByBeyondSelect(): Boolean = ???

    override def getFunctions(p1: String, p2: String, p3: String): ResultSet = ???

    override def supportsSchemasInPrivilegeDefinitions(): Boolean = ???

    override def supportsResultSetConcurrency(p1: Int, p2: Int): Boolean = ???

    override def getURL: String = ???

    override def supportsSubqueriesInQuantifieds(): Boolean = ???

    override def supportsBatchUpdates(): Boolean = false

    override def supportsLikeEscapeClause(): Boolean = ???

    override def supportsExpressionsInOrderBy(): Boolean = ???

    override def allTablesAreSelectable(): Boolean = ???

    override def getCrossReference(p1: String, p2: String, p3: String, p4: String, p5: String, p6: String): ResultSet = ???

    override def getDatabaseMajorVersion: Int = ???

    override def supportsColumnAliasing(): Boolean = ???

    override def getMaxCursorNameLength: Int = ???

    override def getRowIdLifetime: RowIdLifetime = ???

    override def ownDeletesAreVisible(p1: Int): Boolean = ???

    override def supportsDifferentTableCorrelationNames(): Boolean = ???

    override def getDefaultTransactionIsolation: Int = ???

    override def getSearchStringEscape: String = ???

    override def getMaxUserNameLength: Int = ???

    override def supportsANSI92EntryLevelSQL(): Boolean = ???

    override def getProcedureColumns(p1: String, p2: String, p3: String, p4: String): ResultSet = ???

    override def storesMixedCaseQuotedIdentifiers(): Boolean = ???

    override def supportsANSI92FullSQL(): Boolean = ???

    override def getMaxStatementLength: Int = ???

    override def othersDeletesAreVisible(p1: Int): Boolean = ???

    override def supportsTransactions(): Boolean = ???

    override def deletesAreDetected(p1: Int): Boolean = ???

    override def locatorsUpdateCopy(): Boolean = ???

    override def allProceduresAreCallable(): Boolean = ???

    override def getImportedKeys(p1: String, p2: String, p3: String): ResultSet = ???

    override def usesLocalFiles(): Boolean = ???

    override def supportsLimitedOuterJoins(): Boolean = ???

    override def storesMixedCaseIdentifiers(): Boolean = ???

    override def getCatalogTerm: String = ???

    override def getMaxColumnsInGroupBy: Int = ???

    override def supportsSubqueriesInExists(): Boolean = ???

    override def supportsPositionedUpdate(): Boolean = ???

    override def supportsGetGeneratedKeys(): Boolean = ???

    override def supportsUnion(): Boolean = ???

    override def nullsAreSortedLow(): Boolean = ???

    override def getSQLKeywords: String = ???

    override def supportsCorrelatedSubqueries(): Boolean = ???

    override def isReadOnly: Boolean = ???

    override def getProcedures(p1: String, p2: String, p3: String): ResultSet = ???

    override def supportsUnionAll(): Boolean = ???

    override def supportsCoreSQLGrammar(): Boolean = ???

    override def getPseudoColumns(p1: String, p2: String, p3: String, p4: String): ResultSet = ???

    override def getCatalogs: ResultSet = ???

    override def getSuperTables(p1: String, p2: String, p3: String): ResultSet = ???

    override def getMaxColumnsInOrderBy: Int = ???

    override def supportsAlterTableWithAddColumn(): Boolean = ???

    override def getProcedureTerm: String = ???

    override def getMaxCharLiteralLength: Int = ???

    override def supportsMixedCaseIdentifiers(): Boolean = ???

    override def supportsDataDefinitionAndDataManipulationTransactions(): Boolean = ???

    override def supportsCatalogsInProcedureCalls(): Boolean = ???

    override def supportsGroupByUnrelated(): Boolean = ???

    override def getResultSetHoldability: Int = ???

    override def ownUpdatesAreVisible(p1: Int): Boolean = ???

    override def nullsAreSortedHigh(): Boolean = ???

    override def getTables(p1: String, p2: String, p3: String, p4: Array[String]): ResultSet = ???

    override def supportsMultipleTransactions(): Boolean = ???

    override def supportsNamedParameters(): Boolean = ???

    override def getTypeInfo: ResultSet = ???

    override def supportsAlterTableWithDropColumn(): Boolean = ???

    override def getSchemaTerm: String = ???

    override def nullPlusNonNullIsNull(): Boolean = ???

    override def getPrimaryKeys(p1: String, p2: String, p3: String): ResultSet = ???

    override def supportsOpenCursorsAcrossRollback(): Boolean = ???

    override def getMaxBinaryLiteralLength: Int = ???

    override def getExtraNameCharacters: String = ???

    override def getSchemas: ResultSet = ???

    override def getSchemas(p1: String, p2: String): ResultSet = ???

    override def supportsMultipleResultSets(): Boolean = ???

    override def ownInsertsAreVisible(p1: Int): Boolean = ???

    override def nullsAreSortedAtEnd(): Boolean = ???

    override def supportsSavepoints(): Boolean = ???

    override def getMaxStatements: Int = ???

    override def getBestRowIdentifier(p1: String, p2: String, p3: String, p4: Int, p5: Boolean): ResultSet = ???

    override def getDriverVersion: String = ???

    override def storesUpperCaseIdentifiers(): Boolean = ???

    override def storesLowerCaseIdentifiers(): Boolean = ???

    override def getMaxCatalogNameLength: Int = ???

    override def supportsDataManipulationTransactionsOnly(): Boolean = ???

    override def getSystemFunctions: String = ???

    override def getColumnPrivileges(p1: String, p2: String, p3: String, p4: String): ResultSet = ???

    override def getDriverMinorVersion: Int = ???

    override def getMaxTableNameLength: Int = ???

    override def dataDefinitionIgnoredInTransactions(): Boolean = ???

    override def getStringFunctions: String = ???

    override def getMaxColumnsInSelect: Int = ???

    override def usesLocalFilePerTable(): Boolean = ???

    override def autoCommitFailureClosesAllResultSets(): Boolean = ???

    override def supportsCatalogsInIndexDefinitions(): Boolean = ???

    override def storesLowerCaseQuotedIdentifiers(): Boolean = ???

    override def othersUpdatesAreVisible(p1: Int): Boolean = ???

    override def supportsStatementPooling(): Boolean = ???

    override def supportsCatalogsInPrivilegeDefinitions(): Boolean = ???

    override def supportsStoredProcedures(): Boolean = ???

    override def supportsSelectForUpdate(): Boolean = ???

    override def supportsOpenStatementsAcrossCommit(): Boolean = ???

    override def supportsSubqueriesInComparisons(): Boolean = ???

    override def supportsTransactionIsolationLevel(p1: Int): Boolean = ???

    override def getTableTypes: ResultSet = ???

    override def getMaxColumnsInTable: Int = ???

    override def getConnection: Connection = ???

    override def updatesAreDetected(p1: Int): Boolean = ???

    override def supportsPositionedDelete(): Boolean = ???

    override def getColumns(p1: String, p2: String, p3: String, p4: String): ResultSet = ???

    override def supportsResultSetType(p1: Int): Boolean = ???

    override def supportsMinimumSQLGrammar(): Boolean = ???

    override def generatedKeyAlwaysReturned(): Boolean = ???

    override def supportsConvert(): Boolean = ???

    override def supportsConvert(p1: Int, p2: Int): Boolean = ???

    override def getExportedKeys(p1: String, p2: String, p3: String): ResultSet = ???

    override def supportsOrderByUnrelated(): Boolean = ???

    override def getSQLStateType: Int = ???

    override def supportsOpenStatementsAcrossRollback(): Boolean = ???

    override def getMaxColumnsInIndex: Int = ???

    override def getTimeDateFunctions: String = ???

    override def supportsSchemasInIndexDefinitions(): Boolean = ???

    override def supportsANSI92IntermediateSQL(): Boolean = ???

    override def getCatalogSeparator: String = ???

    override def othersInsertsAreVisible(p1: Int): Boolean = ???

    override def supportsSchemasInTableDefinitions(): Boolean = ???

    override def unwrap[T](p1: Class[T]): T = ???

    override def isWrapperFor(p1: Class[_]): Boolean = ???
  }

  override def getTypeMap: util.Map[String, Class[_]] = ???

  override def rollback(): Unit = ???

  override def rollback(p1: Savepoint): Unit = ???

  override def createStatement(p1: Int, p2: Int): Statement = ???

  override def createStatement(p1: Int, p2: Int, p3: Int): Statement = ???

  override def getHoldability: Int = ???

  override def setReadOnly(p1: Boolean): Unit = ???

  override def setClientInfo(p1: String, p2: String): Unit = ???

  override def setClientInfo(p1: Properties): Unit = ???

  override def isReadOnly: Boolean = false

  override def setTypeMap(p1: util.Map[String, Class[_]]): Unit = ???

  override def getCatalog: String = ???

  override def createClob(): Clob = ???

  override def setTransactionIsolation(p1: Int): Unit = ???

  override def nativeSQL(p1: String): String = ???

  override def prepareCall(p1: String): CallableStatement = ???

  override def prepareCall(p1: String, p2: Int, p3: Int): CallableStatement = ???

  override def prepareCall(p1: String, p2: Int, p3: Int, p4: Int): CallableStatement = ???

  override def createArrayOf(p1: String, p2: Array[AnyRef]): sql.Array = ???

  override def setCatalog(p1: String): Unit = ???

  override def close(): Unit = db.close()

  override def getAutoCommit: Boolean = autoCommit

  override def abort(p1: Executor): Unit = ???

  override def isValid(p1: Int): Boolean = ???

  override def prepareStatement(p1: String): sql.PreparedStatement = PreparedStatement(p1)(db)

  override def prepareStatement(p1: String, p2: Int, p3: Int): sql.PreparedStatement = PreparedStatement(p1)(db)

  override def prepareStatement(p1: String, p2: Int, p3: Int, p4: Int): sql.PreparedStatement = PreparedStatement(p1)(db)

  override def prepareStatement(p1: String, p2: Int): sql.PreparedStatement = PreparedStatement(p1)(db)

  override def prepareStatement(p1: String, p2: Array[Int]): sql.PreparedStatement = PreparedStatement(p1)(db)

  override def prepareStatement(p1: String, p2: Array[String]): PreparedStatement = PreparedStatement(p1, p2(0))(db)

  override def releaseSavepoint(p1: Savepoint): Unit = ???

  override def isClosed: Boolean = !db.isOpen

  override def createStruct(p1: String, p2: Array[AnyRef]): Struct = ???

  override def getWarnings: SQLWarning = ???

  override def setSchema(p1: String): Unit = ???

  override def commit(): Unit = ???

  override def unwrap[T](p1: Class[T]): T = ???

  override def isWrapperFor(p1: Class[_]): Boolean = ???
}
