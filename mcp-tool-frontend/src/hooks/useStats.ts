import {useCallback, useEffect, useState} from 'react';
import type { CorpusStats } from '../api/types/stats.ts';
import useSnackbar from "./useSnackbar.ts";
import statsApi from "../api/statsApi.ts";

/**
 * TODO(student): Load the corpus statistics (statsApi.get) for the home
 * dashboard, with loading/error state. This is the same data the provided
 * `corpus_stats` MCP tool returns.
 */
const useStats = () => {
  const {showSnackbar}=useSnackbar();

  const [stats,setStats] = useState<CorpusStats | null>(null);
  const [loading,setLoading] = useState<boolean>(false);

  const fetchStats=useCallback(async ()=>{
    setLoading(true);
    try {
      const response=await statsApi.get();
      setStats(response.data);
    } catch (err){
      showSnackbar(err instanceof Error ? err.message : "Failed to load corpus statistics.",'error');
    } finally {
      setLoading(false);
    }
  },[showSnackbar]);

  useEffect(()=>{
    void fetchStats();
  },[fetchStats]);

  return { stats, loading, refresh: fetchStats };
};

export default useStats;
