package ch.yvu.handyzahlung.provider;

import android.app.Activity;
import android.net.Uri;
import android.provider.BaseColumns;

public class Handyzahlung extends Activity {
    
	public static final String AUTHORITY = "ch.yvu.handyzahlung.provider.Handyzahlung";

    public static final class Empfaenger implements BaseColumns {
        // This class cannot be instantiated
        private Empfaenger() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/empfaenger");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.handyzahlung.empfaenger";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.handyzahlung.empfaenger";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "empfaenger DESC";

        public static final String EMPFAENGER = "empfaenger";
        public static final String BESCHREIBUNG = "beschreibung";
    }
    
    public static final class Zahlung implements BaseColumns {
        // This class cannot be instantiated
        private Zahlung() {}

        //Status
        public static final int STATUS_UNBEKANNT = 0;
        public static final int STATUS_ERFOLGREICH = 1;
        public static final int STATUS_FEHLERHAFT = 2;
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/zahlung");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.handyzahlung.zahlung";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.handyzahlung.zahlung";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "datum DESC";

        public static final String EMPFAENGER = "empfaenger";
        public static final String BETRAG = "betrag";
        public static final String MITTEILUNG = "mitteilung";
        public static final String STATUS = "status";
        public static final String STATUSTEXT = "statustext";
        public static final String DATUM = "datum";
    }
    
    public static final class Saldo implements BaseColumns {
        // This class cannot be instantiated
        private Saldo() {}
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/saldo");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.handyzahlung.saldo";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.handyzahlung.saldo";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "datum DESC";

        public static final String TEXT = "text";
        public static final String DATUM = "datum";
    }
    
    public static final class Kontobewegung implements BaseColumns {
        // This class cannot be instantiated
        private Kontobewegung() {}
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/kontobewegung");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.handyzahlung.kontobewegung";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.handyzahlung.kontobewegung";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "datum DESC";

        public static final String TEXT = "text";
        public static final String DATUM = "datum";
    }
}