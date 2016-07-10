package com.hansong.filter.impl;

import com.hansong.filter.core.AbsFilter;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.core.MessageData;

import java.util.Set;

/**
 * Created by xhans on 2016/7/10 0010.
 */
public class SystemContactFilter extends AbsFilter{


    private static final String TAG = SystemContactFilter.class.getName();

    private Set<String> contacts;

    private SystemContactFilter() {

    }

    @Override
    public int onFiltering(MessageData data) {

        String phone = data.getString(MessageData.KEY_DATA);
        if (contacts.contains(phone)) {
            return IFilter.OP_PASS;
        } else {
            return IFilter.OP_BLOCKED;
        }
    }

    public Set<String> getContacts() {
        return contacts;
    }

    public void setContacts(Set<String> contacts) {
        this.contacts = contacts;
    }

    public static class Builder {
        private SystemContactFilter systemContactFilter;

        public Builder() {
            systemContactFilter = new SystemContactFilter();
        }


        public Builder setContact(Set<String> contact) {
            systemContactFilter.setContacts(contact);
            return this;
        }

        public Builder setStatus(boolean isOpen) {
            if (isOpen) {
                systemContactFilter.open();
            } else {
                systemContactFilter.close();
            }
            return this;
        }

        public SystemContactFilter create() {
            return systemContactFilter;
        }
    }
}
