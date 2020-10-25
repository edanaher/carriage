{ pkgs ? import <nixpkgs> {} }:

with pkgs;

let mymaven = pkgs.maven.override { jdk = pkgs.jdk11; };
in

stdenv.mkDerivation {
  name = "replit-autograder-shell";
  src = ./.;

  buildInputs = [ mymaven pkgs.jdk11 ];

  build="mvn package";
  run="java -classpath .:target/classes/:target/dependencies/* Main";
}

