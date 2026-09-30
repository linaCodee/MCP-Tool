import { useCallback, useEffect, useMemo, useState } from 'react';
import * as React from 'react';
import searchRunApi from '../api/searchRunApi.ts';
import type { RunSearchRequest, SearchRunResponse } from '../api/types/searchRun.ts';
import SearchRunsContext from '../contexts/searchRunsContext.ts';
import useSnackbar from '../hooks/useSnackbar.ts';

/**
 * Fully provided as the reference example of the provider pattern used in
 * this template — mirror it when you build the resources and donations
 * features. Note: until the backend TODO(student) services are implemented,
 * every call surfaces a "Not Implemented" error snackbar.
 */
const SearchRunsProvider = ({ children }: { children: React.ReactNode }) => {
  const { showSnackbar } = useSnackbar();

  const [searchRuns, setSearchRuns] = useState<SearchRunResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const refresh = useCallback(async () => {
    setLoading(true);

    try {
      const response = await searchRunApi.findAll();
      setSearchRuns(response.data);
    } catch (err) {
      showSnackbar(err instanceof Error ? err.message : 'Failed to load search runs.', 'error');
    } finally {
      setLoading(false);
    }
  }, [showSnackbar]);

  const onRun = useCallback(async (data: RunSearchRequest) => {
    try {
      await searchRunApi.run(data);
      await refresh();
    } catch (err) {
      showSnackbar(err instanceof Error ? err.message : 'Failed to run search.', 'error');
    }
  }, [refresh, showSnackbar]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  const value = useMemo(
    () => ({ searchRuns, loading, onRun, refresh }),
    [searchRuns, loading, onRun, refresh]
  );

  return <SearchRunsContext value={value}>{children}</SearchRunsContext>;
};

export default SearchRunsProvider;
