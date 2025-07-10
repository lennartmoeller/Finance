.PHONY: init-git clean-local-branches

init-git:
	git config core.hooksPath .git-hooks
	chmod +x .git-hooks/*

clean-local-branches:
	@git fetch --prune
	@branches_to_delete=$$(git branch -vv | awk '/: gone]/{print $$1}'); \
	if [ -z "$$branches_to_delete" ]; then \
		echo "No local branches to delete."; \
	else \
		echo "Deleting the following local branches:"; \
		echo "$$branches_to_delete"; \
		for branch in $$branches_to_delete; do \
			git branch -D "$$branch"; \
		done \
	fi
