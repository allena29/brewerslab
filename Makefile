BUNDLE_MAKEFILES = $(wildcard bundle/*/Makefile)
BUNDLES = $(dir $(BUNDLE_MAKEFILES))

.PHONY: $(BUNDLES)


all:
	$(MAKE) -C confvillain || exit 1
	for b in $(BUNDLES); do \
		$(MAKE) -C $$b || exit 1; \
	done

install-pyenv:
	git clone https://github.com/pyenv/pyenv.git ~/.pyenv
	git clone https://github.com/pyenv/pyenv-virtualenv.git $(pyenv root)/plugins/pyenv-virtualenv
	pyenv install 2.7.13
	pyenv virtualenv 2.7.13 brewerslab
	./pyenv_bash_profile
  cat .pyenv_bash_profile >>~/.bash_profile	
  
