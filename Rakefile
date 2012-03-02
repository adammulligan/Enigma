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

  Dir.chdir("sys/EnigmaApp") do
    enigma_loc = @build_dir+'/enigma';
    Dir.mkdir(enigma_loc)

    FileUtils.cp_r('./dep',enigma_loc)
    FileUtils.cp_r('./src/',enigma_loc)

    puts 'Running ant...'
    `ant && ant jar`

    FileUtils.cp('./bin/jar/Enigma.jar',enigma_loc)
  end

  # LaTeX compilation
  Dir.chdir("docs/report") do
    # Run latex three times because:
    # http://en.wikibooks.org/wiki/LaTeX/Bibliography_Management#Why_won.27t_LaTeX_generate_any_output.3F
    `pdflatex Thesis.tex && bibtex Thesis && pdflatex Thesis.tex && pdflatex Thesis.tex`
    FileUtils.cp 'Thesis.pdf', "#{@build_dir}/Thesis.pdf"

    latex_src = "#{@build_dir}/latex_src"
    Dir.mkdir(latex_src) unless File.directory?(latex_src)

    ignored_extensions = [".toc",".log",".bbl",".lot",".aux",".blg",".lof",".synctex",".out",".class",".graffle"]
    Find.find("./") do |file|
      next if ignored_extensions.include?(File.extname(file)) || File.fnmatch?('*TSWLatexianTemp*',file)

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
  when 'y' then FileUtils.rm_rf @build_dir  unless !File.exists?(@build_dir)
  end

  jar_dir='sys/EnigmaApp/bin/jar'
  puts "Removing directory #{jar_dir}. Continue?"
  case STDIN.gets.chomp.downcase
  when 'y' then FileUtils.rm_rf jar_dir  unless !File.exists?(jar_dir)
  end

  puts "Removing directory ./project.tgz. Continue?"
  case STDIN.gets.chomp.downcase
  when 'y' then FileUtils.rm_rf "./project.tgz" unless !File.exists?("./project.tgz")
  end
end
