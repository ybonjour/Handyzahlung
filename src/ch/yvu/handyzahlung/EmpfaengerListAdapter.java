package ch.yvu.handyzahlung;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.yvu.handyzahlung.provider.Handyzahlung.Empfaenger;

public class EmpfaengerListAdapter extends CursorAdapter implements Filterable {
	private ContentResolver mContent;
	
	public EmpfaengerListAdapter(Context context, Cursor c) {
        super(context, c);
        mContent = context.getContentResolver();
    }

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
    	final LayoutInflater inflater = LayoutInflater.from(context);
    	final LinearLayout listItem = (LinearLayout) inflater.inflate(android.R.layout.two_line_list_item, parent, false);
    	
    	//Empfänger setzen
    	int idxEmpfaenger = cursor.getColumnIndex(Empfaenger.EMPFAENGER);
    	TextView view1 = ((TextView) listItem.findViewById(android.R.id.text1));
    	view1.setText(cursor.getString(idxEmpfaenger));
    	
    	//Beschreibung setzen
    	int idxBeschreibung = cursor.getColumnIndex(Empfaenger.BESCHREIBUNG);
    	TextView view2 = ((TextView) listItem.findViewById(android.R.id.text2));
    	view2.setText(cursor.getString(idxBeschreibung));
    	
    	return listItem;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		LinearLayout listItem = (LinearLayout) view;
		
		int idxEmpfaenger = cursor.getColumnIndex(Empfaenger.EMPFAENGER);
		TextView view1 = (TextView) listItem.findViewById(android.R.id.text1);
		view1.setText(cursor.getString(idxEmpfaenger));
		
		int idxBeschreibung = cursor.getColumnIndex(Empfaenger.BESCHREIBUNG);
		TextView view2 = (TextView) listItem.findViewById(android.R.id.text2);
		view2.setText(cursor.getString(idxBeschreibung));
	}

	@Override
	public String convertToString(Cursor cursor) {
		int idx = cursor.getColumnIndex(Empfaenger.EMPFAENGER);
		return cursor.getString(idx);
	}

	@Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
    	if (getFilterQueryProvider() != null) {
    		return getFilterQueryProvider().runQuery(constraint);
        }
    		
    	String selection = null;
    	String[] args = null;
    	if(constraint != null)
    	{
    		selection = "UPPER(" + Empfaenger.EMPFAENGER + ") GLOB ?";
    		args = new String[]{constraint.toString().toUpperCase() + "*"};
    	}
    	
    	String[] projection = new String[]{Empfaenger._ID, Empfaenger.EMPFAENGER, Empfaenger.BESCHREIBUNG};
    	return mContent.query(Empfaenger.CONTENT_URI, projection, selection, args, null);
    }       
}