{
  description = "OpenMMO Development Environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
      in
      {
        devShells.default = pkgs.mkShell {
          shellHook = ''
            # Setup opengl + pulseaudio + X11 libraries for PokeMMO to run
            export LD_LIBRARY_PATH=${pkgs.lib.makeLibraryPath [
              pkgs.libGL
              pkgs.libpulseaudio
              pkgs.xorg.libX11
              pkgs.xorg.libXext
              pkgs.xorg.libXi
              pkgs.xorg.libXrender
              pkgs.xorg.libXtst
              pkgs.xorg.libXrandr
              pkgs.freetype
              pkgs.fontconfig
              pkgs.cairo
              pkgs.pango
              pkgs.glib
              pkgs.gtk3
              pkgs.alsa-lib
            ]}:/run/opengl-driver/lib:$LD_LIBRARY_PATH

            echo "OpenMMO dev environment loaded"
          '';
        };
      }
    );
}