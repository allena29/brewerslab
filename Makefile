BUNDLE_MAKEFILES = $(wildcard bundle/*/Makefile)
BUNDLES = $(dir $(BUNDLE_MAKEFILES))

.PHONY: $(BUNDLES)


all:
	$(MAKE) -C confvillain || exit 1
	for b in $(BUNDLES); do \
		$(MAKE) -C $$b || exit 1; \
	done


