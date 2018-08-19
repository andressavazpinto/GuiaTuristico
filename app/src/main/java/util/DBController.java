package util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import model.Search;
import model.User;

/**
 * Created by Andressa on 07/06/2018.
 */

public class DBController {

    private SQLiteDatabase db;

    public DBController(Context context){
        DBHelper helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void insertUser(User u) {
        ContentValues cv = new ContentValues();
        cv.put("idUser", u.getIdUser());
        cv.put("name", u.getName());
        cv.put("dateOfBirth", u.getDateOfBirth());
        cv.put("language", u.getLanguage());
        cv.put("email", u.getEmail());
        cv.put("password", u.getPassword());
        cv.put("idLocalization", u.getIdLocalization());
        cv.put("statusAccount", u.getStatusAccount().toString());

        db.insert("user", null, cv);
        db.close();
    }

    public void insertSearch(Search s) {
        ContentValues cv = new ContentValues();
        cv.put("idUser", s.getIdUser());
        cv.put("status", s.getStatus().toString());

        db.insert("search", null, cv);
        db.close();
    }

    public void updateUser(User u) {
        ContentValues cv = new ContentValues();
        cv.put("name", u.getName());
        cv.put("dateOfBirth", u.getDateOfBirth());
        cv.put("language", u.getLanguage());
        cv.put("email", u.getEmail());
        cv.put("password", u.getPassword());
        cv.put("idLocalization", u.getIdLocalization());
        cv.put("statusAccount", u.getStatusAccount().toString());

        db.update("user", cv, "idUser = ?", new String[]{""+u.getIdUser()});
        db.close();
    }

    public void updateSearch(Search s) {
        ContentValues cv = new ContentValues();
        cv.put("status", s.getStatus().toString());

        db.update("search", cv, "idUser = ?", new String[]{""+s.getIdUser()});
        db.close();
    }

    public void deleteUser(User u) {
        db.delete("user", "idUser = " + u.getIdUser(), null);
    }

    public void deleteSearch(Search s) {
        db.delete("search", "idUser = " + s.getIdUser(), null);
    }

    public User getUser() {
        User u = new User();
        String[] colums = new String[]{"idUser", "name", "dateOfBirth", "language", "email", "idLocalization", "statusAccount"};
        Cursor cursor = db.query("user", colums, null, null, null, null, null);

        if(cursor.getCount() > 0) {
            cursor.moveToNext();

            do {
                u.setIdUser(cursor.getInt(0));
                u.setName(cursor.getString(1));

                String array[];
                array = cursor.getString(2).split("-");
                String aux = array[2]+"/"+array[1]+"/"+array[0];
                u.setDateOfBirth(aux);

                u.setLanguage(cursor.getString(3));
                u.setEmail(cursor.getString(4));
                u.setIdLocalization(cursor.getInt(5));
                u.setStatusAccount(Enum.valueOf(StatusUser.class, "Active"));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return u;
    }

    public Search getSearch() {
        Search s = new Search();
        String[] colums = new String[]{"idUser", "status"};
        Cursor cursor = db.query("search", colums, null, null, null, null, null);

        if(cursor.getCount() > 0) {
            cursor.moveToNext();

            do {
                s.setIdUser(cursor.getInt(0));
                s.setStatus(Enum.valueOf(StatusSearch.class, cursor.getString(1)));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return s;
    }
}
