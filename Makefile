SOURCES = $(shell find src -type f -name "*.java")
.PHONY = all clean

all: out/taskB.zip out/taskC.zip

out/task%.zip: meta/%/Makefile $(SOURCES)
	mkdir -p $(@D)
	zip $@ -r src
	zip $@ -j $<

meta/%/Makefile:
	mkdir -p $(@D)
	@eval "$$MAKEFILE_TEMPLATE"

.PRECIOUS: meta/%/Makefile

clean:
	rm -rf out meta

export
define MAKEFILE_TEMPLATE
cat > $@ <<'EOF'
.PHONY = all run
SOURCES = $(shell find src -type f -name "*.java")
CLASSES = $$(patsubst src/%.java,out/%.class,$$(SOURCES))

all: $$(CLASSES)

out/%.class: src/%.java out
	javac -cp src $$< -d out

out:
	mkdir -p out

run:
	java -cp out Task$(*F)

EOF
endef