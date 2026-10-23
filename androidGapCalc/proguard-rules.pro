# Release builds are shrunk and obfuscated with R8 (see build.gradle.kts).
# The app creates its view-models through hand-written factories and has no reflection of its own,
# so the default Android rules plus the consumer rules shipped with the libraries are enough.
