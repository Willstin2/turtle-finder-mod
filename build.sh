#!/bin/bash

# Turtle Finder Mod Build Script
# This script builds the mod using Maven instead of Gradle

echo "Building Turtle Finder Mod..."

# Clean any previous builds
echo "Cleaning previous builds..."
mvn clean

# Download dependencies and build
echo "Downloading dependencies and building..."
mvn compile

# Package the mod
echo "Packaging mod..."
mvn package

# Check if build was successful
if [ -f "target/turtle-finder-mod-1.0.0.jar" ]; then
    echo "✅ Build successful! Mod jar created at: target/turtle-finder-mod-1.0.0.jar"
    echo ""
    echo "To install:"
    echo "1. Copy target/turtle-finder-mod-1.0.0.jar to your Minecraft mods folder"
    echo "2. Make sure you have Fabric Loader and Fabric API installed"
    echo "3. Launch Minecraft 1.21.4/1.21.5 with Fabric"
    echo ""
    echo "Features:"
    echo "- Shows turtle count on middle-left of screen"
    echo "- Highlights visible turtles with yellow boxes"
    echo "- Draws green lines from crosshair to visible turtles"
    echo "- Detects turtles within 64 block radius (highlights within 32 blocks)"
else
    echo "❌ Build failed! Please check the error messages above."
    exit 1
fi
