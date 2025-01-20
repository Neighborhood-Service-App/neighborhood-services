package com.dragand.neighborhoodservice.jobpost.model.jobPost;


import com.dragand.neighborhoodservice.jobpost.model.applicant.Applicant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_posts")
public class JobPost {

    /**
     * The unique identifier for the job post.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private UUID jobPostId;

    /**
     * The user who created the job post.
     */
    @Column(nullable = false)
    private UUID creatorId;

    /**
     * The title of the job post.
     */
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Column(nullable = false)
    private String title;

    /**
     * The description of the job post.
     */
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column
    private String description;

// Essential address fields directly embedded
    /**
     * Full address of the job. E.g. Levka Lukianenka St, 29, Kyiv, Ukraine, 04205
     */
    @NotBlank(message = "Address cannot be blank")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(nullable = false)
    private String address;

    /**
     * Latitude of the job.
     */
    @NotNull
    @Column
    private Double latitude;

    /**
     * Longitude of the job.
     */
    @NotNull
    @Column
    private Double longitude;

    /**
     * Job status.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobPostStatus status;

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Applicant> applicants = new ArrayList<>();


    /**
     * The timestamp when the job post was created. It is set automatically when a new job post is created.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the job post was last updated. It is set automatically when a new job post is updated.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

}
