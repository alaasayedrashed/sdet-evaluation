package com.sdet.evaluation.mobile.models;

/**
 * Data entered on the "register a new User" screen. Immutable, so the values typed in one step
 * can be safely compared with what the confirmation screen shows in a later step.
 */
public record UserRegistration(
        String username,
        String email,
        String password,
        String name,
        String programmingLanguage,
        boolean acceptAdds) {
}
