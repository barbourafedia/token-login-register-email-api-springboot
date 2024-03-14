package com.ecommerce.ecommerce.registration;

import org.hibernate.annotations.NaturalId;

import java.util.Date;

/**
 * @author Sampson Alfred
 */

public record RegistrationRequest(
         String nom,
         String email,
         String password,
         Date datecreation,
         String profil) {
}
