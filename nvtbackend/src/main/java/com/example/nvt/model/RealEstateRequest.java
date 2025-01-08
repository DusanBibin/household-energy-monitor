package com.example.nvt.model;


import com.example.nvt.enumeration.RequestStatus;
import com.example.nvt.enumeration.RequestType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class RealEstateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client requester;

    @OneToOne
    private Realestate realestate;

    @ManyToOne
    private Admin reviewingAdmin;

    @ElementCollection
    @CollectionTable(name = "proof_realestate_img", joinColumns = @JoinColumn(name = "realestate_request_id"))
    @Column(name = "proof_img_url")
    private List<String> proof_images;


    @ElementCollection
    @CollectionTable(name = "realestate_pdf", joinColumns = @JoinColumn(name = "realestate_request_id"))
    @Column(name = "proof_pdf_url")
    private List<String> proof_pdfs;


    private RequestStatus requestStatus;
    private RequestType requestType;
    private LocalDateTime requestSubmitted;
    private LocalDateTime requestProcessed;
    private String denialReason;

}
