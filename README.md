# Zirconium

![Zirconium logo](https://cdn.modrinth.com/data/cached_images/ceefbb78281023540c2b0239021fa6be045d330e_0.webp)

[![GitHub Release](https://img.shields.io/github/release/Kubik-Modder/zirconium-fabric.svg)](https://github.com/Kubik-Modder/zirconium-fabric/releases)
![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)
![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)

Zirconium is a **server-side** Minecraft mod designed to enhance performance by optimizing chunk loading, terrain generation, and biome processing through multithreading. By offloading these intensive tasks to multiple threads, Zirconium reduces server lag, ensuring a smoother and more seamless gaming experience for players.

## Features

- **Multithreaded Chunk Loading:** Efficiently handles chunk loading across multiple threads to minimize lag.
- **Optimized Terrain Generation:** Enhances the speed and efficiency of terrain generation processes.
- **Biome Processing:** Streamlines biome processing to ensure smooth transitions and performance.
- **Server-Side Only:** Operates exclusively on the server, requiring no client-side installations.
- **Fabric-Compatible:** Built using the Fabric mod loader framework for seamless integration.

## Performance

### With mod

- **TPS (Ticks Per Second):** Stable at 20.00 across all time windows.
- **MSPT (Milliseconds per Tick):** The median is 5.46 ms with a maximum of 52.1 ms.
- **Memory:** The mod uses 1.1 GB out of 2 GB (57.03%).

![With mod](https://cdn.modrinth.com/data/cached_images/ef4682500c1660b047a1f9db5ba94148a6f294b6.png)

### Without mod

- **TPS:** Slight fluctuation between 19.97 and 20.00.
- **MSPT:** The median is 7.32 ms, with a significant spike to 364 ms at its peak. This suggests that without the mod, certain operations (like chunk loading or generation) cause major delays.
- **Memory:** Memory usage increases to 1.1 GB out of 1.4 GB (77.92%), indicating higher memory pressure without the mod.

![Without mod](https://cdn.modrinth.com/data/cached_images/32ed9111f113aa04d44553b1e937b33204082048.png)

Credit: [SparkProfiler](https://modrinth.com/mod/spark)

## Installation

### Prerequisites

- **Minecraft Version:** Ensure your Minecraft instance is running a compatible mod version (e.g., 1.20.1).
- **Fabric Loader:** Zirconium is built on Fabric. Install the Fabric Loader if you haven't already.
- **Fabric API:** Install the Fabric API mod, which Zirconium depends on.

### Steps

1. **Download Zirconium:**
   - Visit the [Releases](https://github.com/Kubik-Modder/zirconium-fabric/releases) page and download the latest `zirconium.jar` file.

2. **Install Fabric Loader:**
   - If you haven't installed Fabric, download the Fabric installer from the [official website](https://fabricmc.net/).
   - Run the installer, select a compatible Minecraft version, and install it.

3. **Install Fabric API:**
   - Download the latest Fabric API from [Modrinth](https://modrinth.com/mod/fabric-api).
   - Place the `fabric-api.jar` file into your server's `mods` folder.

4. **Add Zirconium to Mods Folder:**
   - Place the downloaded `zirconium.jar` file into your server's `mods` folder.

## Compatibility

- **Mod Loader:** Zirconium is compatible exclusively with the **Fabric** mod loader. It is **not** compatible with other mod loaders such as Forge.
- **Minecraft Versions:** Ensure that your server's Minecraft version matches the versions supported by Zirconium. Refer to the [Releases](https://github.com/Kubik-Modder/zirconium-fabric/releases) page for version compatibility.
- **Client-Side:** Zirconium is a **server-side only** mod. Players do **not** need to install any additional mods to connect to a server running Zirconium.

## Contributing

I welcome contributions from the community! To contribute to Zirconium, please follow the guidelines outlined in my [CONTRIBUTING.md](https://github.com/Kubik-Modder/zirconium-fabric/blob/Alpha/CONTRIBUTING.md) file.

### How to Contribute

1. **Report Issues:** If you encounter bugs or have suggestions for improvements, please [open an issue](https://github.com/Kubik-Modder/zirconium-fabric/issues) with detailed information.
2. **Submit Pull Requests:** Fork the repository, make your changes, and submit a pull request following the [Pull Request Template](https://github.com/Kubik-Modder/zirconium-fabric/blob/Alpha/.github/PULL_REQUEST_TEMPLATE/pull_request.md).

### Code Guidelines

- **Indentation:** Use 4 spaces for indentation. Do not use tabs.
- **Comments:** Utilize Javadoc-style comments for classes, methods, and significant code blocks.
- **Naming Conventions:** Follow standard Java naming conventions for classes, methods, and variables.
- **Documentation:** Ensure all Javadoc comments are accurate and up-to-date.

## License

Zirconium is licensed under the [GNU General Public License v3.0](https://www.fsf.org/).

By contributing to Zirconium, you agree that your contributions will also be licensed under the GNU GPL v3.0.

## Support

If you need help or have questions about Zirconium, feel free to reach out:

- **Issues:** [Open an Issue](https://github.com/Kubik-Modder/zirconium-fabric/issues)
- **Contact:** You can contact me directly via [kubik.modder@gmail.com](mailto:kubik.modder@gmail.com).

## Acknowledgments

- **Fabric Team:** For providing a robust mod loader framework.
- **Minecraft Community:** For continuous support and feedback.
- **Contributors:** Thanks to all the contributors who have helped improve Zirconium.

---

**Thank you for using Zirconium!** Your support helps me continue to enhance and optimize Minecraft performance.

