export class ExtURL {
    private readonly url: URL;

    constructor(url?: string | URL | ExtURL, base?: string | URL | ExtURL) {
        const urlAsString: string = typeof url === 'string' ? url : url?.toString() ?? window.location.href;
        const baseAsString: string | undefined = typeof base === 'string' ? base : base?.toString();
        this.url = new URL(urlAsString, baseAsString);
    }

    hasSearchParam(key: string): boolean {
        return this.url.searchParams.has(key);
    }

    getSearchParam(key: string): string | null {
        return this.url.searchParams.get(key);
    }

    setSearchParam(key: string, value: string): void {
        this.url.searchParams.set(key, value);
    }

    deleteSearchParam(key: string): void {
        this.url.searchParams.delete(key);
    }

    hasSearchParamMap(key: string): boolean {
        for (const spKey of this.url.searchParams.keys()) {
            if (spKey.startsWith(`${key}.`)) {
                return true;
            }
        }
        return false;
    }

    getSearchParamMap(key: string): Record<string, string> {
        const result: Record<string, string> = {};
        this.url.searchParams.forEach((value: string, spKey: string) => {
            if (spKey.startsWith(`${key}.`) && value) {
                result[spKey.substring(key.length + 1)] = value;
            }
        });
        return result;
    }

    setSearchParamMap(key: string, values: Record<string, string | null>): void {
        Object.entries(values).forEach(([subKey, value]) => {
            if (!value) {
                this.url.searchParams.delete(`${key}.${subKey}`);
            } else {
                this.url.searchParams.set(`${key}.${subKey}`, value);
            }
        });
    }

    deleteSearchParamMap(key: string): void {
        this.url.searchParams.forEach((value: string, spKey: string) => {
            if (spKey.startsWith(`${key}.`)) {
                this.url.searchParams.delete(spKey);
            }
        });
    }

    toCurrent(): void {
        window.history.replaceState(null, '', this.url.toString());
    }

    toString(): string {
        return this.url.toString();
    }

}
