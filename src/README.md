# Source components for automated review

`src/` is intentionally organized one-to-one with `doc/*_design.md`, as required by the Sprint Reviewer Agent.
It mirrors the same source files used by the runnable project; the native build/deployment folders remain at repository root so Arduino, Gradle and Plesk continue to work without path rewrites.

Do not treat `src/` as a second implementation: it is the reviewer-normalized view of the same Sprint 0 code.
