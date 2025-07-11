.PHONY: init-git clean-local-branches

init-git:
	git config core.hooksPath .git-hooks
	chmod +x .git-hooks/*
