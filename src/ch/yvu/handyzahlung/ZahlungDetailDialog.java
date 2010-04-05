package ch.yvu.handyzahlung;

import java.text.DateFormat;
import java.util.Date;
import ch.yvu.handyzahlung.provider.Handyzahlung.Zahlung;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ZahlungDetailDialog extends Dialog {
	
	private Uri uri;
	private TextView mEmpfaenger;
	private TextView mBetrag;
	private TextView mMitteilung;
	private TextView mDatum;
	private TextView mStatus;
	private TextView mStatusText;
	
	public ZahlungDetailDialog(Context context, Uri uri) {
		super(context);
		this.uri = uri;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_zahlungdetail);
		
		setTitle(R.string.Details);
		//Beschreibung holen
		String[] projection = new String[]{Zahlung._ID, Zahlung.EMPFAENGER, Zahlung.BETRAG, Zahlung.MITTEILUNG, Zahlung.DATUM, Zahlung.STATUS, Zahlung.STATUSTEXT}; 
		Cursor cursor = getContext().getContentResolver().query(this.uri, projection, null, null, null);
		cursor.moveToFirst();
		
		int idxEmpfaenger = cursor.getColumnIndex(Zahlung.EMPFAENGER);
		String strEmpfaenger = cursor.getString(idxEmpfaenger);
		this.mEmpfaenger = (TextView) findViewById(R.id.zahlungDetailEmpfaenger);
		this.mEmpfaenger.setText(strEmpfaenger);
		
		int idxBetrag = cursor.getColumnIndex(Zahlung.BETRAG);
		String strBetrag = cursor.getString(idxBetrag);
		this.mBetrag = (TextView) findViewById(R.id.zahlungDetailBetrag);
		this.mBetrag.setText(strBetrag);
		
		int idxMitteilung = cursor.getColumnIndex(Zahlung.MITTEILUNG);
		String strMitteilung = cursor.getString(idxMitteilung);
		this.mMitteilung = (TextView) findViewById(R.id.zahlungDetailMitteilung);
		this.mMitteilung.setText(strMitteilung);
		
		int idxDatum = cursor.getColumnIndex(Zahlung.DATUM);
		long timeStamp = cursor.getInt(idxDatum);
		Date datum = new Date(timeStamp);
		DateFormat df = DateFormat.getDateTimeInstance();
		this.mDatum = (TextView) findViewById(R.id.zahlungDetailDatum);
		this.mDatum.setText(df.format(datum));
				
		int idxStatus = cursor.getColumnIndex(Zahlung.STATUS);
		int status = cursor.getInt(idxStatus);
		String strStatus;
		switch(status)
		{
		case Zahlung.STATUS_ERFOLGREICH:
			strStatus = getContext().getResources().getString(R.string.Erfolgreich);
			break;
		case Zahlung.STATUS_FEHLERHAFT:
			strStatus = getContext().getResources().getString(R.string.Fehlerhaft);
			break;
		default:
			strStatus = getContext().getResources().getString(R.string.Unbekannt);
			break;	
		}
		this.mStatus = (TextView) findViewById(R.id.zahlungDetailStatus);
		this.mStatus.setText(strStatus);
		
		int idxStatusText = cursor.getColumnIndex(Zahlung.STATUSTEXT);
		String strStatusText = cursor.getString(idxStatusText);
		this.mStatusText = (TextView) findViewById(R.id.zahlungDetailStatusText);
		this.mStatusText.setText(strStatusText);
		
		Button buttonOK = (Button) findViewById(R.id.ZahlungDetailButtonOK);
		buttonOK.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {				
				dismiss();
			}
		});
	}
}
