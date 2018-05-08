package gr.uom.pam.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable{
    private String _path;
    private String _name;

    Category(String name, String path) {
        _name = name;
        _path = path;
    }

    private Category(Parcel in) {
        _path = in.readString();
        _name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_path);
        dest.writeString(_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String get_path() {
        return _path;
    }

    public void set_path(String _path) {
        this._path = _path;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return _path != null && _path.equals(((Category) o)._path) ;
    }

    @Override
    public int hashCode() {
        return _path != null ? _path.hashCode() : 0;
    }

}
