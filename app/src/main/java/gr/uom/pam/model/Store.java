package gr.uom.pam.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Store implements Parcelable {
    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };
    private String _path;
    private String _name;

    Store(String name, String path) {
        _name = name;
        _path = path;
    }

    private Store(Parcel in) {
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
        return _path != null && _path.equals(((Store) o)._path);
    }

    @Override
    public int hashCode() {
        return _path != null ? _path.hashCode() : 0;
    }
}
