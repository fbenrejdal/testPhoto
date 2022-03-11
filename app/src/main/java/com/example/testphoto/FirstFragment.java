package com.example.testphoto;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.testphoto.databinding.FragmentFirstBinding;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    public Bitmap openPhoto(String contactId) {
        Bitmap bitmap;

        try {
            AssetFileDescriptor fd =
                    getContext().getContentResolver().openAssetFileDescriptor(Uri.parse(contactId), "r");
            System.out.println("OpenPhoto returning fd "+fd);
            bitmap=BitmapFactory.decodeStream(fd.createInputStream());
            fd.close();
            return bitmap;
        } catch (IOException e) {
            System.out.println("OpenPhoto return Null");
            return null;
        }
    }
    public long getContactIDFromNumber(Context context, View view) {
        long phoneContactID = 0;
        Uri contactNumberUri=ContactsContract.Data.CONTENT_URI;

        Cursor contactLookupCursor = context.getContentResolver().query(contactNumberUri, null, ContactsContract.Data.MIMETYPE + "= 'vnd.android.cursor.item/phone_v2'", null, null);
        ImageView myImageview = (ImageView) view.findViewById(R.id.imageView);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getLong(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.Data._ID));
            String mimeType = contactLookupCursor.getString(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE));
            String phoneNumber = contactLookupCursor.getString(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DATA1));
            String photo=contactLookupCursor.getString(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.Data.PHOTO_URI));

            System.out.println("phoneContactID "+phoneContactID);
            System.out.println("minetype "+mimeType);
            System.out.println("PhoneNumber "+phoneNumber);
            System.out.println("PhotoID ("+photo+")");

            if (photo !=null) {
                Bitmap bitmap = openPhoto(photo);
                if (bitmap != null)
                    myImageview.setImageBitmap(bitmap);
            }
        }
        contactLookupCursor.close();

        return phoneContactID;
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        System.out.println("Calling getContactIDFromNumber");
        getContactIDFromNumber(getContext(),view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}