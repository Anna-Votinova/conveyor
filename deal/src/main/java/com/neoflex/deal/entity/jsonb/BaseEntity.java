package com.neoflex.deal.entity.jsonb;

import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.TypeDef;

import javax.persistence.MappedSuperclass;

@TypeDef(name = "json", typeClass = JsonType.class)
@MappedSuperclass
public class BaseEntity {
}
