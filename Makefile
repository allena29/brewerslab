BUNDLE_MAKEFILES = $(wildcard bundle/*/Makefile)
BUNDLES = $(dir $(BUNDLE_MAKEFILES))

.PHONY: $(BUNDLES)


all:
	$(MAKE) -C confvillain || exit 1
	for b in $(BUNDLES); do \
		$(MAKE) -C $$b || exit 1; \
	done

install-pyenv:
	./pyenv_installer
 
tempfs:
	sudo mount -t tmpfs -o size=50M tmpfs heap/running
	touch heap/running/.gitkeep
