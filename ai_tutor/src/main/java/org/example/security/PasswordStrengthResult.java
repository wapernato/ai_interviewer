package org.example.security;

import java.util.List;

public record PasswordStrengthResult(
   PasswordStrengthLevel level,
   List<String> suggestions
) {}
