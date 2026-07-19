package bg.sofia.uni.fmi.mjt.server.rest;

public record APICandidate(String sha256, boolean revealedInExposure, int exposureCount) {
}
