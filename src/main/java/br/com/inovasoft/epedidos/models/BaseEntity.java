package br.com.inovasoft.epedidos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@Audited
@Getter @Setter
@MappedSuperclass
@TypeDefs({
		@TypeDef(name = "json", typeClass = JsonStringType.class),
		@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public abstract class BaseEntity extends PanacheEntityBase implements Serializable {
    
	private static final long serialVersionUID = -7179704060635002164L;

    @JsonIgnore
	@CreationTimestamp
    private LocalDateTime createdOn;
    
    @JsonIgnore
	@UpdateTimestamp
	private LocalDateTime updatedOn;
    
    @JsonIgnore
	private LocalDateTime deletedOn;

	public boolean isActive(){
		return deletedOn == null;
	}

}