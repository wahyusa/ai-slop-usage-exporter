plugins {
    // Upgraded to 8.3.2 to support newer Gradle versions
    id("com.android.application") version "8.3.2" apply false
    
    // Upgraded to 1.9.24 (Fixes the HasConvention error)
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}