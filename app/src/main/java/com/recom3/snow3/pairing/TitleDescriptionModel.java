package com.recom3.snow3.pairing;

import java.io.Serializable;

/**
 * Created by Recom3 on 17/07/2022.
 */

public class TitleDescriptionModel implements Serializable {
    private static final long serialVersionUID = 3713749197807895796L;

    private String mDescription;

    private String mTitle;

    public TitleDescriptionModel(String paramString1, String paramString2) {
        this.mTitle = paramString1;
        this.mDescription = paramString2;
    }

    public boolean equals(Object paramObject) {
        boolean bool = true;
        if (this != paramObject) {
            if (paramObject == null)
                return false;
            if (getClass() != paramObject.getClass())
                return false;
            paramObject = paramObject;
            if (this.mDescription == null) {
                if (((TitleDescriptionModel)paramObject).mDescription != null)
                    return false;
            } else if (!this.mDescription.equals(((TitleDescriptionModel)paramObject).mDescription)) {
                return false;
            }
            if (this.mTitle == null) {
                if (((TitleDescriptionModel)paramObject).mTitle != null)
                    bool = false;
                return bool;
            }
            if (!this.mTitle.equals(((TitleDescriptionModel)paramObject).mTitle))
                bool = false;
        }
        return bool;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public int hashCode() {
        int j;
        int i = 0;
        if (this.mDescription == null) {
            j = 0;
        } else {
            j = this.mDescription.hashCode();
        }
        if (this.mTitle != null)
            i = this.mTitle.hashCode();
        return (j + 31) * 31 + i;
    }
}
