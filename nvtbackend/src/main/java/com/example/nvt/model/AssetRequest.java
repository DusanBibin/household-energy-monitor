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
@Inheritance(strategy = InheritanceType.JOINED)
public class AssetRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client requester;


    @ManyToOne
    private Admin reviewingAdmin;

    @ElementCollection
    @CollectionTable(name = "proof_img", joinColumns = @JoinColumn(name = "proof_file_id"))
    @Column(name = "proof_img_url")
    private List<String> proof_images;


    @ElementCollection
    @CollectionTable(name = "proof_pdf", joinColumns = @JoinColumn(name = "proof_file_id"))
    @Column(name = "proof_pdf_url")
    private List<String> proof_pdfs;


    private RequestStatus requestStatus;
    private RequestType requestType;
    private LocalDateTime requestSubmitted;
    private LocalDateTime requestProcessed;
    private String denialReason;

}
