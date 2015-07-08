# patricia-xero-sync

Sample webapp to sync Patricia financial exports to xero org

The following environment variables are needed to run:

	PATRICIA_DB_JDBC_URL="jdbc:jtds:sqlserver://a.database:1433;databaseName=Patricia5;useCursors=true"
	PATRICIA_DB_SQL_DRIVER="net.sourceforge.jtds.jdbc.Driver"
	PATRICIA_DB_USERNAME="aUserHere"
	PATRICIA_DB_PASSWORD="aPasswordHere"
	XERO_CONSUMER_KEY="from-xero-api-page"
	XERO_CONSUMER_SECRET="from-xero-api-page"
	XERO_PRIVATE_KEY_PATH="/path/to/private/xerokey.pem"


