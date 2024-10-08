package com.help.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractModel {

    @Column(name = "uuid", nullable = false, unique = true, length = 36, updatable = false)
    private String uuid;

    public String getUuid() {
        ensureUUID();
        return uuid;
    }

    private void ensureUUID() {
        if (uuid == null) {
            setUUID(UUID.randomUUID().toString());
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    @PrePersist
    private void prePersist() {
        ensureUUID();
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractModel that = (AbstractModel) obj;
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}

