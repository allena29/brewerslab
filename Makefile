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
	[[ `uname` = 'Darwin' ]] && RD=`hdiutil attach -nomount ram://99000`;newfs_hfs -v 'cv-heap' $$RD;mount -o noatime -t hfs $$RD heap/running/ || echo 'Not running Darwin'
	[[ `uname` = 'Linux' ]] && sudo mount -t tmpfs -o size=50M tmpfs heap/running || echo 'Not running Linux'
	touch heap/running/.gitkeep
