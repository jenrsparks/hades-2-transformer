# Hades 2 JSON Converter (for Save Files)

*First and foremost: this project could not exist without [Hades-SavesExtractor](https://github.com/TheNormalnij/Hades-SavesExtractor). The input file is the direct output of this program.*

---

**Note:** Compiling the SavesExtractor on my Mac was a headache, but for posterity and maximum usability, here's a rundown on how:

1. Clone the repository.
2. Run `git submodule update --init --recursive` to account for files that may not have come down right.
3. Create a `build` folder with `mkdir build`.
4. From the `build` folder, run `clang++ -std=c++17 -I../src ../src/main.cpp -L. -lHadesSavesExtractorLib -o HadesSavesExtractor`.
5. Run `make`.
6. Find the executable in `bin/` folder.
