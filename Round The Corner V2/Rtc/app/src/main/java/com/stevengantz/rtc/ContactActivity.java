package com.stevengantz.rtc;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity {

    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }

    protected void onStart(){
        super.onStart();
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    /**
     * Runs when activity ends in onStart()
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICK_CONTACT_REQUEST){
            if(resultCode == RESULT_OK){
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                Log.d("contact", String.valueOf(column));
                String number = cursor.getString(column);
                Toast.makeText(getApplicationContext(), "You selected " + number, Toast.LENGTH_LONG).show();
                DataHandler.phoneNumber = number;
                startActivity(ActivityManager.openDistanceActivity(getApplicationContext()));
            }
        }
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }
}
