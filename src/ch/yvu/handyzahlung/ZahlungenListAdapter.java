package ch.yvu.handyzahlung;

import java.text.DateFormat;
import java.util.Date;

import ch.yvu.handyzahlung.provider.Handyzahlung.Zahlung;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ZahlungenListAdapter extends SimpleCursorAdapter{

	public ZahlungenListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		
		int idxStatus = cursor.getColumnIndex(Zahlung.STATUS);
		int status = cursor.getInt(idxStatus);
		
		ImageView image = (ImageView) view.findViewById(R.id.ZahlungStatusIcon);
		
		Drawable icon;
		switch(status)
		{
		case Zahlung.STATUS_ERFOLGREICH:
			icon = context.getResources().getDrawable(android.R.drawable.presence_online);
			break;
		case Zahlung.STATUS_FEHLERHAFT:
			icon = context.getResources().getDrawable(android.R.drawable.presence_offline);
			break;
		case Zahlung.STATUS_UNBEKANNT:
		default:
			icon = context.getResources().getDrawable(android.R.drawable.presence_invisible);
		}
		
		image.setImageDrawable(icon);
		
		//Datum formatieren
		int idxDatum = cursor.getColumnIndex(Zahlung.DATUM);
		long timeStamp = cursor.getLong(idxDatum);
		Date datum = new Date(timeStamp);
		DateFormat df = DateFormat.getDateTimeInstance();
				
		TextView viewDatum = (TextView) view.findViewById(R.id.ZahlungDatum);
		viewDatum.setText(df.format(datum));
	}
}
