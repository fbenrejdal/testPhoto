package com.example.testphoto;

import android.content.ContentUris;
import android.content.Context;
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
import java.util.Random;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    public Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContext().getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;

    }
    public static long getContactIDFromNumber(String contactNumber, Context context) {
        String UriContactNumber = Uri.encode(contactNumber);
        long phoneContactID = new Random().nextInt();
        /*Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, UriContactNumber),
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);*/
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, UriContactNumber),
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getLong(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            System.out.println("phoneContactID "+phoneContactID);
            System.out.println("UriContactNumber "+UriContactNumber);
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

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        ImageView myImageview = (ImageView) view.findViewById(R.id.imageView);

        Bitmap bitmap = openPhoto(getContactIDFromNumber("21300",getContext()));
        myImageview.setImageBitmap(bitmap);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}