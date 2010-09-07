package ch.yvu.handyzahlung.provider;

import java.util.HashMap;

import ch.yvu.handyzahlung.provider.Handyzahlung.Empfaenger;
import ch.yvu.handyzahlung.provider.Handyzahlung.Kontobewegung;
import ch.yvu.handyzahlung.provider.Handyzahlung.Saldo;
import ch.yvu.handyzahlung.provider.Handyzahlung.Zahlung;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class HandyzahlungProvider extends ContentProvider{

	private static final String TAG = "Handyzahlulngprovider";

    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 23;
    private static final String EMPFAENGER_TABLE_NAME = "empfaenger";
    private static final String ZAHLUNG_TABLE_NAME = "zahlung";
    private static final String SALDO_TABLE_NAME = "saldo";
    private static final String KONTOBEWEGUNG_TABLE_NAME = "kontobewegung";
    
    private static final int EMPFAENGER = 1;
    private static final int EMPFAENGER_ID = 2;
    
    private static final int ZAHLUNG = 3;
    private static final int ZAHLUNG_ID = 4;
    
    private static final int SALDO = 5;
    private static final int SALDO_ID = 6;
    
    private static final int KONTOBEWEGUNG = 7;
    private static final int KONTOBEWEGUNG_ID = 8;
    
    private static HashMap<String, String> sEmpfaengerProjectionMap;
    private static HashMap<String, String> sZahlungProjectionMap;
    private static HashMap<String, String> sSaldoProjectionMap;
    private static HashMap<String, String> sKontobewegungProjectionMap;
    private static final UriMatcher sUriMatcher;
      
    static {
    	sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    	sUriMatcher.addURI(Handyzahlung.AUTHORITY, "empfaenger", EMPFAENGER);
    	sUriMatcher.addURI(Handyzahlung.AUTHORITY, "empfaenger/#", EMPFAENGER_ID);
    	sUriMatcher.addURI(Handyzahlung.AUTHORITY, "zahlung", ZAHLUNG);
    	sUriMatcher.addURI(Handyzahlung.AUTHORITY, "zahlung/#", ZAHLUNG_ID);
    	sUriMatcher.addURI(Handyzahlung.AUTHORITY, "saldo", SALDO);
    	sUriMatcher.addURI(Handyzahlung.AUTHORITY, "saldo/#", SALDO_ID);
    	sUriMatcher.addURI(Handyzahlung.AUTHORITY, "kontobewegung", KONTOBEWEGUNG);
    	sUriMatcher.addURI(Handyzahlung.AUTHORITY, "kontobewegung/#", KONTOBEWEGUNG_ID);
    	
        sEmpfaengerProjectionMap = new HashMap<String, String>();
        sEmpfaengerProjectionMap.put(Empfaenger._ID, Empfaenger._ID);
        sEmpfaengerProjectionMap.put(Empfaenger.EMPFAENGER, Empfaenger.EMPFAENGER);
        sEmpfaengerProjectionMap.put(Empfaenger.BESCHREIBUNG, Empfaenger.BESCHREIBUNG);
        
        sZahlungProjectionMap = new HashMap<String, String>();
        sZahlungProjectionMap.put(Zahlung._ID, ZAHLUNG_TABLE_NAME + "." + Zahlung._ID);
        sZahlungProjectionMap.put(Zahlung.EMPFAENGER, EMPFAENGER_TABLE_NAME + "." + Empfaenger.EMPFAENGER);
        sZahlungProjectionMap.put(Zahlung.BETRAG, ZAHLUNG_TABLE_NAME + "." + Zahlung.BETRAG);
        sZahlungProjectionMap.put(Zahlung.MITTEILUNG, ZAHLUNG_TABLE_NAME + "." + Zahlung.MITTEILUNG);
        sZahlungProjectionMap.put(Zahlung.STATUS, ZAHLUNG_TABLE_NAME + "." + Zahlung.STATUS);
        sZahlungProjectionMap.put(Zahlung.STATUSTEXT, ZAHLUNG_TABLE_NAME + "." + Zahlung.STATUSTEXT);
        sZahlungProjectionMap.put(Zahlung.DATUM, ZAHLUNG_TABLE_NAME + "." + Zahlung.DATUM);
        
        sSaldoProjectionMap = new HashMap<String, String>();
        sSaldoProjectionMap.put(Saldo._ID, SALDO_TABLE_NAME + "." + Saldo._ID);
        sSaldoProjectionMap.put(Saldo.TEXT, SALDO_TABLE_NAME + "." + Saldo.TEXT);
        sSaldoProjectionMap.put(Saldo.DATUM, SALDO_TABLE_NAME + "." + Saldo.DATUM);
        
        sKontobewegungProjectionMap = new HashMap<String, String>();
        sKontobewegungProjectionMap.put(Kontobewegung._ID, KONTOBEWEGUNG_TABLE_NAME + "." + Kontobewegung._ID);
        sKontobewegungProjectionMap.put(Kontobewegung.TEXT, KONTOBEWEGUNG_TABLE_NAME + "." + Kontobewegung.TEXT);
        sKontobewegungProjectionMap.put(Kontobewegung.DATUM, KONTOBEWEGUNG_TABLE_NAME + "." + Kontobewegung.DATUM);
    }
    
    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }
    
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		int count;
		
		switch (sUriMatcher.match(uri)) {
		case EMPFAENGER:
			count = db.delete(EMPFAENGER_TABLE_NAME, where, whereArgs);
			break;
		case EMPFAENGER_ID:
			String empfaengerId = uri.getPathSegments().get(1);
			count = db.delete(EMPFAENGER_TABLE_NAME, Empfaenger._ID + "=" + empfaengerId + (!TextUtils.isEmpty(where) ? "AND (" + where + ")" : ""), whereArgs);
			break;
		case ZAHLUNG:
			count = db.delete(ZAHLUNG_TABLE_NAME, where, whereArgs);
			break;
		case ZAHLUNG_ID:
			String zahlungId = uri.getPathSegments().get(1);
			count = db.delete(ZAHLUNG_TABLE_NAME, Zahlung._ID + "=" + zahlungId +(!TextUtils.isEmpty(where) ? "AND (" + where + ")" : ""), whereArgs);
			break;
		case SALDO:
			count = db.delete(SALDO_TABLE_NAME, where, whereArgs);
			break;
		case SALDO_ID:
			String saldoId = uri.getPathSegments().get(1);
			count = db.delete(SALDO_TABLE_NAME, Saldo._ID + "=" + saldoId + (!TextUtils.isEmpty(where) ? "AND (" + where + ")" : ""), whereArgs);
			break;
		case KONTOBEWEGUNG:
			count = db.delete(KONTOBEWEGUNG_TABLE_NAME, where, whereArgs);
			break;
		case KONTOBEWEGUNG_ID:
			String kontobewegungId = uri.getPathSegments().get(1);
			count = db.delete(KONTOBEWEGUNG_TABLE_NAME, Kontobewegung._ID + "=" + kontobewegungId + (!TextUtils.isEmpty(where) ? "AND (" + where + ")" : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
	    return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case EMPFAENGER:
			return Empfaenger.CONTENT_TYPE;
		case EMPFAENGER_ID:
			return Empfaenger.CONTENT_ITEM_TYPE;
		case ZAHLUNG:
			return Zahlung.CONTENT_TYPE;
		case ZAHLUNG_ID:
			return Zahlung.CONTENT_ITEM_TYPE;
		case SALDO:
			return Saldo.CONTENT_TYPE;
		case SALDO_ID:
			return Saldo.CONTENT_ITEM_TYPE;
		case KONTOBEWEGUNG:
			return Kontobewegung.CONTENT_TYPE;
		case KONTOBEWEGUNG_ID:
			return Kontobewegung.CONTENT_ITEM_TYPE;
		default:
		    throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
				
		switch(sUriMatcher.match(uri))
		{
		case EMPFAENGER:
			return insertEmpfaenger(initialValues);
		case ZAHLUNG:
			return insertZahlung(initialValues);
		case SALDO:
			return insertSaldo(initialValues);
		case KONTOBEWEGUNG:
			return insertKontobewegung(initialValues);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);	
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch(sUriMatcher.match(uri))
		{
		case EMPFAENGER:
		case EMPFAENGER_ID:
			return queryEmpfaenger(uri, sortOrder, projection, selection, selectionArgs);	
		case ZAHLUNG:
		case ZAHLUNG_ID:
			return queryZahlung(uri, sortOrder, projection, selection, selectionArgs);
		case SALDO:
		case SALDO_ID:
			return querySaldo(uri, sortOrder, projection, selection, selectionArgs);
		case KONTOBEWEGUNG:
		case KONTOBEWEGUNG_ID:
			return queryKontobewegung(uri, sortOrder, projection, selection, selectionArgs);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		switch(sUriMatcher.match(uri)){
		case EMPFAENGER:
		case EMPFAENGER_ID:
			return updateEmpfaenger(uri, values, where, whereArgs);
		case ZAHLUNG:
		case ZAHLUNG_ID:
			return updateZahlung(uri, values, where, whereArgs);
		case SALDO:
		case SALDO_ID:
			return updateSaldo(uri, values, where, whereArgs);
		case KONTOBEWEGUNG:
		case KONTOBEWEGUNG_ID:
			return updateKontobewegung(uri, values, where, whereArgs);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	private Uri insertZahlung(ContentValues initialValues) {
		ContentValues values = initialValues != null ? new ContentValues(initialValues) : new ContentValues();
		
		if(!values.containsKey(Zahlung.EMPFAENGER)) throw new IllegalArgumentException("Failed to insert. Empfaenger not set.");
		if(!values.containsKey(Zahlung.BETRAG)) values.put(Zahlung.BETRAG, "-");
		if(!values.containsKey(Zahlung.MITTEILUNG)) values.put(Zahlung.MITTEILUNG, "");
		if(!values.containsKey(Zahlung.STATUS)) values.put(Zahlung.STATUS, Zahlung.STATUS_UNBEKANNT);
		if(!values.containsKey(Zahlung.STATUSTEXT)) values.put(Zahlung.STATUSTEXT, "");
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(ZAHLUNG_TABLE_NAME, null, values);
		if(rowId <= 0) throw new SQLException("Failed to insert row");
		
		Uri zahlungUri = ContentUris.withAppendedId(Zahlung.CONTENT_URI, rowId);
		getContext().getContentResolver().notifyChange(zahlungUri, null);
		
		return zahlungUri;
	}

	private Uri insertEmpfaenger(ContentValues initialValues) {
		ContentValues values = initialValues != null ? new ContentValues(initialValues) : new ContentValues();
		
		if(!values.containsKey(Empfaenger.EMPFAENGER)) values.put(Empfaenger.EMPFAENGER, "-");
		if(!values.containsKey(Empfaenger.BESCHREIBUNG)) values.put(Empfaenger.BESCHREIBUNG, "");
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(EMPFAENGER_TABLE_NAME, null, values);
		if(rowId <= 0) throw new SQLException("Failed to insert row");
		
		Uri empfaengerUri = ContentUris.withAppendedId(Empfaenger.CONTENT_URI, rowId);
		getContext().getContentResolver().notifyChange(empfaengerUri, null);
		
		return empfaengerUri;
	}
	
	private Uri insertSaldo(ContentValues initialValues) {
		ContentValues values = initialValues != null ? new ContentValues(initialValues) : new ContentValues();
		
		if(!values.containsKey(Saldo.DATUM)) throw new IllegalArgumentException("Failed to insert. Datum not set.");
		if(!values.containsKey(Saldo.TEXT)) values.put(Saldo.TEXT, "");
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(SALDO_TABLE_NAME, null, values);
		if(rowId <= 0) throw new SQLException("Failed to insert row");
		
		Uri saldoUri = ContentUris.withAppendedId(Saldo.CONTENT_URI, rowId);
		getContext().getContentResolver().notifyChange(saldoUri, null);
		
		return saldoUri;
	}
	
	private Uri insertKontobewegung(ContentValues initialValues) {
		ContentValues values = initialValues != null ? new ContentValues(initialValues) : new ContentValues();
		
		if(!values.containsKey(Kontobewegung.TEXT)) throw new IllegalArgumentException("Failed to insert. Text not set.");
		if(!values.containsKey(Kontobewegung.TEXT)) values.put(Kontobewegung.TEXT, "");
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(KONTOBEWEGUNG_TABLE_NAME, null, values);
		if(rowId <= 0) throw new SQLException("Failed to insert row");
		
		Uri kontobewegungUri = ContentUris.withAppendedId(Kontobewegung.CONTENT_URI, rowId);
		getContext().getContentResolver().notifyChange(kontobewegungUri, null);
		
		return kontobewegungUri;
	}
	
	private int updateEmpfaenger(Uri uri, ContentValues values, String whereClause, String[] whereArgs)
	{
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		int count;
		
		if(sUriMatcher.match(uri) == EMPFAENGER_ID)
		{
			String empfaengerId = uri.getPathSegments().get(1);
			whereClause =  Empfaenger._ID + "=" + empfaengerId + (!TextUtils.isEmpty(whereClause) ? " AND (" + whereClause + ")": "");
		}
		
		count = db.update(EMPFAENGER_TABLE_NAME, values, whereClause, whereArgs);
			
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	private int updateZahlung(Uri uri, ContentValues values, String whereClause, String[] whereArgs)
	{
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		int count;
		
		if(sUriMatcher.match(uri) == ZAHLUNG_ID)
		{
			String zahlungId = uri.getPathSegments().get(1);
			whereClause =  Zahlung._ID + "=" + zahlungId + (!TextUtils.isEmpty(whereClause) ? " AND (" + whereClause + ")": "");
		}
		
		count = db.update(ZAHLUNG_TABLE_NAME, values, whereClause, whereArgs);
			
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	private int updateSaldo(Uri uri, ContentValues values, String whereClause, String[] whereArgs)
	{
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		int count;
		
		if(sUriMatcher.match(uri) == SALDO_ID)
		{
			String saldoId = uri.getPathSegments().get(1);
			whereClause =  Saldo._ID + "=" + saldoId + (!TextUtils.isEmpty(whereClause) ? " AND (" + whereClause + ")": "");
		}
		
		count = db.update(SALDO_TABLE_NAME, values, whereClause, whereArgs);
			
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	private int updateKontobewegung(Uri uri, ContentValues values, String whereClause, String[] whereArgs)
	{
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		int count;
		
		if(sUriMatcher.match(uri) == KONTOBEWEGUNG_ID)
		{
			String kontobewegungId = uri.getPathSegments().get(1);
			whereClause =  Kontobewegung._ID + "=" + kontobewegungId + (!TextUtils.isEmpty(whereClause) ? " AND (" + whereClause + ")": "");
		}
		
		count = db.update(KONTOBEWEGUNG_TABLE_NAME, values, whereClause, whereArgs);
			
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	private Cursor queryEmpfaenger(Uri uri, String sortOrder, String[] projection, String selection, String[] selectionArgs)
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(EMPFAENGER_TABLE_NAME);
		qb.setProjectionMap(sEmpfaengerProjectionMap);
		
		if(sUriMatcher.match(uri) == EMPFAENGER_ID) qb.appendWhere(Empfaenger._ID + "=" + uri.getPathSegments().get(1)); 
		
		// If no sort order is specified use the default
		String orderBy = TextUtils.isEmpty(sortOrder) ? Empfaenger.DEFAULT_SORT_ORDER : sortOrder;
		
		return executeQuery(uri, qb, projection, selection, selectionArgs, orderBy);
	}
	
	private Cursor queryZahlung(Uri uri, String sortOrder, String[] projection, String selection, String[] selectionArgs)
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		//zahlung inner join empfaenger on (zahlung.empfaenger=empfaenger.id)
		qb.setTables(ZAHLUNG_TABLE_NAME + " INNER JOIN " + EMPFAENGER_TABLE_NAME + " ON (" + ZAHLUNG_TABLE_NAME + "." + Zahlung.EMPFAENGER + "=" + EMPFAENGER_TABLE_NAME + "." + Empfaenger._ID + ")");
		qb.setProjectionMap(sZahlungProjectionMap);
		
		if(sUriMatcher.match(uri) == ZAHLUNG_ID) qb.appendWhere(ZAHLUNG_TABLE_NAME + "." + Zahlung._ID + "=" + uri.getPathSegments().get(1)); 
		
		// If no sort order is specified use the default
		String orderBy = TextUtils.isEmpty(sortOrder) ? ZAHLUNG_TABLE_NAME + "." + Zahlung.DEFAULT_SORT_ORDER : sortOrder;
		
		return executeQuery(uri, qb, projection, selection, selectionArgs, orderBy);
	}
	
	private Cursor querySaldo(Uri uri, String sortOrder, String[] projection, String selection, String[] selectionArgs)
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(SALDO_TABLE_NAME);
		qb.setProjectionMap(sSaldoProjectionMap);
		
		if(sUriMatcher.match(uri) == SALDO_ID) qb.appendWhere(Saldo._ID + "=" + uri.getPathSegments().get(1)); 
		
		// If no sort order is specified use the default
		String orderBy = TextUtils.isEmpty(sortOrder) ? Saldo.DEFAULT_SORT_ORDER : sortOrder;
		
		return executeQuery(uri, qb, projection, selection, selectionArgs, orderBy);
	}
	
	private Cursor queryKontobewegung(Uri uri, String sortOrder, String[] projection, String selection, String[] selectionArgs)
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(KONTOBEWEGUNG_TABLE_NAME);
		qb.setProjectionMap(sKontobewegungProjectionMap);
		
		if(sUriMatcher.match(uri) == KONTOBEWEGUNG_ID) qb.appendWhere(Kontobewegung._ID + "=" + uri.getPathSegments().get(1)); 
		
		// If no sort order is specified use the default
		String orderBy = TextUtils.isEmpty(sortOrder) ? Kontobewegung.DEFAULT_SORT_ORDER : sortOrder;
		
		return executeQuery(uri, qb, projection, selection, selectionArgs, orderBy);
	}
	
	private Cursor executeQuery(Uri uri, SQLiteQueryBuilder qb, String[] projection, String selection, String[] selectionArgs, String orderBy)
	{
		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		
		// Tell the cursor what uri to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {        	
            //Tabelle Empfänger erstellen
        	db.execSQL("CREATE TABLE IF NOT EXISTS " + EMPFAENGER_TABLE_NAME + " ("
                    + Empfaenger._ID + " INTEGER PRIMARY KEY,"
                    + Empfaenger.EMPFAENGER + " TEXT,"
                    + Empfaenger.BESCHREIBUNG + " TEXT"
                    + ");");
        	
        	//Tabelle Zahlung erstellen
        	db.execSQL("CREATE TABLE IF NOT EXISTS " + ZAHLUNG_TABLE_NAME + " ("
                    + Zahlung._ID + " INTEGER PRIMARY KEY,"
                    + Zahlung.EMPFAENGER + " INTEGER REFERENCES " + EMPFAENGER_TABLE_NAME + " (" + Empfaenger._ID + ") ON DELETE CASCADE,"
                    + Zahlung.BETRAG + " TEXT,"
                    + Zahlung.MITTEILUNG + " TEXT,"
                    + Zahlung.STATUS + " INTEGER,"
                    + Zahlung.STATUSTEXT + " TEXT,"
                    + Zahlung.DATUM + " INTEGER"
                    + ");");
        	        	
        	//Trigger wird benötigt, da SQLite keine
        	//ForeignKey Constraints durchsetzt
        	db.execSQL("CREATE TRIGGER IF NOT EXISTS fkd_zahlung_empfaenger_id " +
        			"BEFORE DELETE ON " + EMPFAENGER_TABLE_NAME + " " +
        			"FOR EACH ROW BEGIN " +
        			" DELETE from " + ZAHLUNG_TABLE_NAME + " WHERE empfaenger = OLD." + Empfaenger._ID + "; " +
        			"END;");
        	
        	//Tabelle Saldo erstellen
        	db.execSQL("CREATE TABLE IF NOT EXISTS " + SALDO_TABLE_NAME + " ("
                    + Saldo._ID + " INTEGER PRIMARY KEY,"
                    + Saldo.TEXT + " TEXT,"
                    + Saldo.DATUM + " INTEGER"
                    + ");");
        	
        	//Tabelle Kontobewegung erstellen
        	db.execSQL("CREATE TABLE IF NOT EXISTS " + KONTOBEWEGUNG_TABLE_NAME + " ("
                    + Kontobewegung._ID + " INTEGER PRIMARY KEY,"
                    + Kontobewegung.TEXT + " TEXT,"
                    + Kontobewegung.DATUM + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion);

            onCreate(db);
          
        }
    }
}