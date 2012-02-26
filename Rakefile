require 'rake'
require 'rubygems'
require 'git'
require 'logger'
require 'zlib'
require 'archive/tar/minitar'
require 'find'

include Archive::Tar

@build_dir = Dir.getwd + '/dist'

desc 'Build project for distribution'
task :build do
  # Check if git branch has changes
  # if it doesn't continue, otherwise throw an error
  # latex Thesis.tex
  # bibtex Thesis.aux
  # mkdir build
  # cp:
  #   README
  #   docs/report/Thesis.pdf
  #     mkdir build/latex_src
  #     build latex from source
  #     cp docs/report/* ./build/latex_src (minus logs, etc)
  # javac sys/EnigmaApp/src
  # cp sys/EnigmaApp
  #
  # Final structure should be:
  #
  # build/
  #   Enigma/
  #     src/
  #     bin/
  #     dep/
  #     res/
  #   report/
  #     Thesis.pdf
  #     latex_src/
  #   README.md
  Dir.mkdir(@build_dir) unless File.directory?(@build_dir)

  # LaTeX compilation
  Dir.chdir("docs/report") do
    # Run latex three times because:
    # http://en.wikibooks.org/wiki/LaTeX/Bibliography_Management#Why_won.27t_LaTeX_generate_any_output.3F
    `pdflatex Thesis.tex && bibtex Thesis && pdflatex Thesis.tex && pdflatex Thesis.tex`
    FileUtils.cp 'Thesis.pdf', "#{@build_dir}/Thesis.pdf"

    latex_src = "#{@build_dir}/latex_src"
    Dir.mkdir(latex_src) unless File.directory?(latex_src)

    ignored_extensions = [".toc",".log",".bbl",".lot",".aux",".blg",".lof",".synctex",".out",".class"]
    Find.find("./") do |file|
      next if ignored_extensions.include?(File.extname(file)) || File.fnmatch?('*TSWLatexianTemp*',file)

      puts file

      if File.directory?(file)
        FileUtils.cp_r file, latex_src+'/'+file
      else
        FileUtils.cp file, latex_src+'/'+file
      end
    end
  end

  tgz = Zlib::GzipWriter.new(File.open('project.tgz', 'wb'))
  Minitar.pack(@build_dir, tgz)
end

task :clean do
  cleanup
end

task :default => "build"

def cleanup
  puts "Removing directory #{@build_dir}. Continue?"

  case STDIN.gets.chomp.downcase
  when 'y' then FileUtils.rm_rf @build_dir
  end
end
