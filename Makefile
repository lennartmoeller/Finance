.PHONY: init-git

init-git:
	git config core.hooksPath .git-hooks
	chmod +x .git-hooks/*
