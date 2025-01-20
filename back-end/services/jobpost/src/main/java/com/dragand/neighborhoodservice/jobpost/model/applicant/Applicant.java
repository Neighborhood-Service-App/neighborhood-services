package com.dragand.neighborhoodservice.jobpost.model.applicant;

import com.dragand.neighborhoodservice.jobpost.model.jobPost.JobPost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "applicants")
public class Applicant {

    /**
     * The unique identifier for the applicant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private UUID applicantId;

    /**
     * The id of user who applied for the job.
     */
    @NotNull
    @Column(nullable = false)
    private UUID userId; // ID of the user applying for the job

    /**
     * The job post to which the user applied.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;


//    For now, message is simplified to a string. In the future, it can be expended to a separate file(PDF, WORD, TXT).
    /**
     * The message of the application. A quick message or cover letter.
     */
    @Column(nullable = false)
    private String message; // Application message or cover letter

    /**
     * The status of the application. Can be PENDING, ACCEPTED, REJECTED.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private ApplicantStatus status;

}
