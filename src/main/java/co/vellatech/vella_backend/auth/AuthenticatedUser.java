package co.vellatech.vella_backend.auth;

public record AuthenticatedUser(String subject, String role, Long storeId, Long customerId) {
}
