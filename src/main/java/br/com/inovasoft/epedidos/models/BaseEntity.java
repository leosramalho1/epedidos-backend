package br.com.inovasoft.epedidos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter @Setter
@MappedSuperclass
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