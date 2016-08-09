package com.aptoide.models.displayables;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;

import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.stores.Login;

public class StoreItem extends Displayable implements Parcelable {

	public final Login login;
	public String storeName;
	public String storeDwnNumber;
	public String storeAvatar;
	public boolean list;
	public long id;
	@ColorRes private int storeHeaderColor;
	private int themeId;

	public StoreItem(String name, String number, String avatar, @ColorRes int storeHeaderColor, int themeId, boolean list, long id, Login login, int bucketSize) {
		super(bucketSize);
		this.login = login;
		this.storeName = name;
		this.storeDwnNumber = number;
		this.storeAvatar = avatar;
		this.list = list;
		this.id = id;
		FULL_ROW = bucketSize;
		this.storeHeaderColor = storeHeaderColor;
		this.themeId = themeId;
		setSpanSize(1);
	}

	protected StoreItem(Parcel in) {
		super(in);
		login = in.readParcelable(Login.class.getClassLoader());
		storeName = in.readString();
		storeDwnNumber = in.readString();
		storeAvatar = in.readString();
		list = in.readByte() != 0;
		id = in.readLong();
		storeHeaderColor = in.readInt();
		themeId = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeParcelable(login, flags);
		dest.writeString(storeName);
		dest.writeString(storeDwnNumber);
		dest.writeString(storeAvatar);
		dest.writeByte((byte) (list ? 1 : 0));
		dest.writeLong(id);
		dest.writeInt(storeHeaderColor);
		dest.writeInt(themeId);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<StoreItem> CREATOR = new Creator<StoreItem>() {
		@Override
		public StoreItem createFromParcel(Parcel in) {
			return new StoreItem(in);
		}

		@Override
		public StoreItem[] newArray(int size) {
			return new StoreItem[size];
		}
	};

	@Override
	public int getSpanSize() {
		return 1;
	}

	public
	@ColorRes
	int getStoreHeaderColor() {
		return storeHeaderColor;
	}

	public int getThemeId() {
		return themeId;
	}
}