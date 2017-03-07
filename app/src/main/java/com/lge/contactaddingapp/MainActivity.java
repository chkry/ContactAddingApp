package com.lge.contactaddingapp;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.Contacts;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static String PHONE_ACCOUNT_NAME = "com.lge.sync"; //phone-contact
    public static String SIM_ACCOUNT_NAME = "com.android.contacts.sim"; //sim-contact

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editText = (EditText)findViewById(R.id.editText);
        final Context cntx = getApplicationContext();




            // Pass below variable <em>cntx</em> into the WritePhoneContact() function as third variable.
		 // get application context
            //Now call below function to do the real task for you.
        ImageButton imgbtn = (ImageButton)findViewById(R.id.imageButton);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editText.getText().toString();
                if(!phoneContactExists(cntx,"TARGETA")){
                    WritePhoneContact("TARGETA", phoneNumber ,cntx);
               }else{
                    Toast.makeText(getApplicationContext(),"Contact Found",Toast.LENGTH_SHORT).show();
               }

            }
        });

        //String myname = getContactNameByNumber("9059057059");
       // Toast.makeText(getApplicationContext(),myname,Toast.LENGTH_LONG).show();
    } // E.O.OnCreate


//    public String getContactNameByNumber(String number) {
//        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
//        String name = "?";
//
//        ContentResolver contentResolver = getContentResolver();
//        Cursor contact = contentResolver.query(uri, new String[] {BaseColumns._ID,
//                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
//
//        try {
//            if (contact != null && contact.getCount() > 0) {
//                contact.moveToNext();
//                name = contact.getString(contact.getColumnIndex(Data.DISPLAY_NAME));
//            }
//        } finally {
//            if (contact != null) {
//                contact.close();
//            }
//        }
//
//        return name;
//    }



    public static final String LOG_TAG = "CONTACT CHECKER";

    private static boolean findContact(Context mContext, String DISPLAY_NAME, String ACCOUNT_NAME) {
        if (DISPLAY_NAME != null) {
            Uri uri = RawContacts.CONTENT_URI;
            String[] projection = new String[] 	{ RawContacts._ID	};
            String selection = RawContacts.ACCOUNT_TYPE + "= \'" + ACCOUNT_NAME +
                    "\' AND " +
                    RawContacts.DISPLAY_NAME_PRIMARY + "= \'"+ DISPLAY_NAME + "\'";
            String[] selectionArgs = null;
            String sortOrder = null;
            Cursor cur = mContext.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
            try {
                if (cur.moveToFirst()) {
                    for (int i=0; i<cur.getColumnCount(); i++) {
                        Log.i(LOG_TAG + "_PKON", "contactExists(): DATA(col=" + i + ")=" + cur.getString(i) );
                    }
                    if (false == cur.moveToNext()) { //query resulted in only one row. Only one contact present.
                        Log.i("SEROTONIN", "contactExists(): FOUND: DISPLAY_NAME=" + DISPLAY_NAME + " ACCOUNT_NAME=" + ACCOUNT_NAME);
                        return true;
                    }
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
        }
        return false;
    } //e.o.findContact


    public static boolean phoneContactExists(Context mContext, String inStr) {
        return findContact(mContext, inStr, PHONE_ACCOUNT_NAME);
    } //e.o.phoneContactExists




    public void WritePhoneContact(String displayName, String number,Context cntx /*App or Activity Ctx*/)
    {
        Context contetx 	= cntx; //Application's context or Activity's context
        String strDisplayName 	=  displayName; // Name of the Person to add
        String strNumber 	=  number; //number of the person to add with the Contact

        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        //Newly Inserted contact
     // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)//Step1
                .withValue(RawContacts.ACCOUNT_TYPE, PHONE_ACCOUNT_NAME)
                .withValue(RawContacts.ACCOUNT_NAME, PHONE_ACCOUNT_NAME).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)//Step2
                .withValueBackReference(Data.RAW_CONTACT_ID,contactIndex)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                .build());
        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, strNumber) // Number to be added
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc
        try
        {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = contetx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
        }
        catch (RemoteException exp)
        {
            //logs;
        }
        catch (OperationApplicationException exp)
        {
            //logs
        }
    }
















} //E.O.Activity
