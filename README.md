[![Unit Test](https://github.com/jenrsparks/hades-2-transformer/actions/workflows/maven-test.yml/badge.svg)](https://github.com/jenrsparks/hades-2-transformer/actions/workflows/maven-test.yml) [![Package](https://github.com/jenrsparks/hades-2-transformer/actions/workflows/maven-package.yml/badge.svg)](https://github.com/jenrsparks/hades-2-transformer/actions/workflows/maven-package.yml)

# Hades 2 Save File Transformer

Convert your Hades 2 save files from `lua` format to `json` or `yaml`, applying data transformations to get your preferred structure and using Supergiant's own localization of terms for better readability.

*This project **absolutely** could not exist without the [Hades-SavesExtractor](https://github.com/TheNormalnij/Hades-SavesExtractor), and was equally inspired by such. A big thank you and shout-out to [TheNormalnij](https://github.com/TheNormalnij) for their hard work and dedication.*

## Project Status

This project is in active development. Current features include:

- LUA to JSON/YAML conversion with format detection based on output file
- Command-line interface with robust input handling
- JOLT transformation support with custom specs

Future functionality coming soon:

- Multi-language trait support
- Custom language dictonary overrides

Non-functional efforts pending:

- Create build & publish solution for packages ready to use
- Clean up debug- and development-related residuals throughout the program.

## Prerequisites

To compile the application from source, you will need Maven 3.6 or higher.

To run this application, you will need Java 25 or higher, along with a Hades 2 save file extracted using `Hades-SavesExtractor`.

## Usage

```bash
java -cp target/classes io.github.jenrsparks.hades.App [options]
```

Or if using the packaged JAR:

```bash
java -jar target/hades-2-json-converter-1.0-SNAPSHOT.jar [options]
```

### Command Line Options

- `-i, --input <file>`: Input LUA file (default: `save1.lua`)
- `-o, --output <file>`: Output file name (default: `save1.json`); supports JSON, YAML, and YML
- `-s, --spec <file>`: JOLT spec JSON file for data transformation (optional); defaults to a 1:1 mapping of structure in the format of the specified file
- `-h, --help`: Show help message
- `-V, --version`: Show version information

### Examples

The below examples are written assuming you're working with a pre-built package of the application.

- Convert a save file to JSON:

  ```bash
  java -jar -i save2.lua -o save2.json
  ```

Convert to YAML:

```bash
java -cp target/classes io.github.jenrsparks.hades.App -i save2.lua -o save2.yaml
```

Use a custom transformation spec:

```bash
java -cp target/classes io.github.jenrsparks.hades.App -i save2.lua -o save2.json -s custom-spec.json
```

## Limitations

- Requires pre-extracted LUA files from Hades-SavesExtractor
- Output format is determined by file extension (.json or .yaml)
- Limited to Hades 2 save files
- Some advanced LUA constructs may not be fully supported

## Gotchas

- Ensure the input LUA file is properly extracted using Hades-SavesExtractor
- The application creates a temporary JSON file (`save2_temp.json`) by default for debugging purposes
- Large save files may take time to process
- Make sure you have sufficient disk space for output files

## Installation Steps

If you're looking to compile it yourself, the following steps should get you there quickly. Double-check the version requirements listed in "Prerequisites" before executing the below steps.

   ```bash
   # Clone the repository
   git clone https://github.com/jenrsparks/hades-2-transformer.git
   cd hades-2-transformer

   # Compile the project:
   mvn clean compile
   
   # (Optional) Run tests:
   mvn test
   
   # Build the JAR:
   mvn package
   ```

Once completed successfully, you should be able to run commands as specified in the "Usage" section.

### Bonus: Compiling Hades-SavesExtractor on a Mac

[Hades-SavesExtractor](https://github.com/TheNormalnij/Hades-SavesExtractor) has distribution files available for both Windows and Linux, however for my own use, I'm running exclusively on a Mac, which left me in a bit of a bind. As such, I wanted to share the steps I followed to get this compiling successfully, despite some odd hiccups I encountered:

1. Clone the repository.
2. Run `git submodule update --init --recursive` to account for files that may not have come down right. (Repeated check-outs continuously had this issue; it definitely was not a fluke.)
3. Create a `build` folder with `mkdir build`.
4. From the `build` folder, run `clang++ -std=c++17 -I../src ../src/main.cpp -L. -lHadesSavesExtractorLib -o HadesSavesExtractor`.
5. Run `make`.
6. Find the executable in `bin/` folder.

## Frequently Asked Questions

- **Why do I need to use Hades-SavesExtractor first?**

  The Hades-SavesExtractor application converts the binary save files to `.lua` format. This application expects the file to be in `.lua` format.

- **Can I convert save files from other games?**  

  Currently only Hades 2 save files are supported. (This will *likely* work for the original Hades with the right custom spec & dictionary provided, however it has not been tested!)

- **How do I handle large save files?**

  The tool should handle most save files, but ensure you have sufficient RAM and disk space.

## Issues and Support

If you encounter any issues or have questions:

- Check the [Issues](https://github.com/jenrsparks/hades-2-transformer/issues) page
- Create a new issue with detailed information about your problem
- Include your Java version, Maven version, and sample input/output files when reporting bugs

## Acknowledgments

- [Hades-SavesExtractor](https://github.com/TheNormalnij/Hades-SavesExtractor) - For the LUA extraction functionality
- [Supergiant Games](https://www.supergiantgames.com/) - For creating Hades 2

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
