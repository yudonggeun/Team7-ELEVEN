import create from 'zustand';

interface PageStore {
    currentPage: string;
    setPage: (page: string) => void;
}

export const usePageStore = create<PageStore>((set) => ({
    currentPage: 'home', // 기본 페이지를 '경매'로 설정
    setPage: (page: string) => set({ currentPage: page }),
}));
