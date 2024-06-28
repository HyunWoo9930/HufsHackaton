package org.example.hufshackaton.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "sports")
public class Sports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    @JsonIgnore
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String country;

    @OneToMany(mappedBy = "sports", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Step> steps = new HashSet<>();

    public void addStep(Step step) {
        steps.add(step);
        step.setSports(this);
    }

    public void removeStep(Step step) {
        steps.remove(step);
        step.setSports(null);
    }
}
